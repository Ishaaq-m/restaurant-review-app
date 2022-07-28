package com.example.restaurantreviewapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var userLocation: Location
    private lateinit var mMap: GoogleMap
    private val LOCATION_CODE = 1
    private val cancellationTokenSource: CancellationTokenSource = CancellationTokenSource()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val confirmBtn = findViewById<MaterialButton>(R.id.confirm_button)
        val cancelBtn = findViewById<MaterialButton>(R.id.cancel_btn)

        confirmBtn.setOnClickListener {
            passToActivity(userLocation)
        }

        cancelBtn.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        getLocation()
    }

    private fun getLocation() {
        val mapView = findViewById<ViewGroup>(R.id.map_layout)

        if (isUserLocationEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_CODE)
                return
            }
            mMap.isMyLocationEnabled = true

            val currentLocation: Task<Location> = fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
            currentLocation.addOnSuccessListener { location ->
                if (location != null) {
                    userLocation = location
                    val currentLatLong = LatLng(location.latitude, location.longitude)
                    locationPointer(currentLatLong)
                }
            }
        }
        else {
            Snackbar.make(mapView, R.string.location_disabled_msg, Snackbar.LENGTH_SHORT).show()

            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    //This is to handle the case where the user accepts/denies the location permissions
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val view = findViewById<ViewGroup>(R.id.map_layout)

        when(requestCode) {
            LOCATION_CODE  -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if ((ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED)) {
                        getLocation()
                    }
                }
                else {
                    Snackbar.make(view, R.string.location_denied_msg, Snackbar.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun isUserLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    //Creates a marker with the user's latitude and longitude values and displays them along with the address it maps to
    private fun locationPointer(latlong: LatLng) {
        val pointer = MarkerOptions().position(latlong).title("$latlong")
            .snippet(getAddress(latlong.latitude, latlong.longitude))

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong, 15f))
        mMap.addMarker(pointer)
    }

    override fun onMarkerClick(p0: Marker) = false

    private fun getAddress(lat: Double, lon: Double): String {
        val geoCode = Geocoder(this, Locale.getDefault())
        val addresses = geoCode.getFromLocation(lat, lon, 1)
        return addresses[0].getAddressLine(0).toString()
    }

    //Cancel the sending of result back to the activity that requested for data if back button is pressed
    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    //Pass the result back to the activity that started this one for a result
    private fun passToActivity(location: Location) {
        val data = Intent()
        data.putExtra("address", getAddress(location.latitude, location.longitude))
        setResult(Activity.RESULT_OK, data)
        finish()
    }
}