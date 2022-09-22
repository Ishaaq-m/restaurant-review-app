package com.example.restaurantreviewapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class AddRestaurantActivity : AppCompatActivity() {

    //Retrieve image selected from gallery
    private val getImageGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { result: Uri? ->
        val attachedImage = findViewById<ImageView>(R.id.attached_image)
        if (result != null) {
            attachedImage.setImageURI(result)
            uri = result
        }
    }

    //Retrieve address from MapActivity
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val address = intent?.getStringExtra("address")

                if (address != null) {
                    restaurantAddress = address
                    displayAddress(address)
                }
            }
        }

    private lateinit var auth: FirebaseAuth
    private lateinit var storageRef: FirebaseStorage
    private lateinit var imageUri: String
    private var uri: Uri? = null
    private var restaurantAddress: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_restaurant)

        auth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance()

        val submitBtn = findViewById<MaterialButton>(R.id.submit_btn)
        val cancelBtn = findViewById<MaterialButton>(R.id.cancel_btn)
        val locationBtn = findViewById<MaterialButton>(R.id.tag_location_btn)
        val imageBtn = findViewById<MaterialButton>(R.id.add_image_btn)

        submitBtn.setOnClickListener {
            validateFields()
        }

        cancelBtn.setOnClickListener {
            finish()
        }

        locationBtn.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startForResult.launch(intent)
        }

        imageBtn.setOnClickListener {
            imagePicker()
        }
    }

    private fun validateFields() {
        val restaurantNameInput = findViewById<TextInputEditText>(R.id.restaurant_name_input)
        val categoryInput = findViewById<TextInputEditText>(R.id.category_input)

        val view = findViewById<ViewGroup>(R.id.create_restaurant)

        val restaurantName = restaurantNameInput.text.toString().trim()
        val category = categoryInput.text.toString().trim()

        if (restaurantName.isEmpty()) {
            restaurantNameInput.error = getString(R.string.restaurant_name_error)
        }
        else if (category.isEmpty()) {
            categoryInput.error = getString(R.string.category_empty_msg)
        }
        else if (restaurantName.length < 3) {
            restaurantNameInput.error = getString(R.string.restaurant_length_error)
        }
        else if (category.length < 3) {
            categoryInput.error = getString(R.string.category_length_msg)
        }
        else if (restaurantAddress.isBlank()) {
            Snackbar.make(view, getString(R.string.location_required_message), Snackbar.LENGTH_SHORT).show()
        }
        else {
            createRestaurant(view, restaurantName, category)
            finish()
        }
    }

    private fun imagePicker() {
        getImageGallery.launch("image/*")
    }

    private fun createRestaurant(view: View, restaurantName: String, category: String) {
        val db = Firebase.firestore
        val user = auth.currentUser

        if (user != null) {

            val newRestaurant = hashMapOf(
                "name" to restaurantName,
                "address" to restaurantAddress,
                "category" to category
            )

            db.collection("restaurants")
                .document(restaurantName)
                .set(newRestaurant)
                .addOnSuccessListener {
                    uploadImage(restaurantName)
                    Log.d("addRestaurant", "Restaurant added")

                    Snackbar.make(view, R.string.restaurant_added_msg, Snackbar.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.w("addRestaurant", "Error adding restaurant", e)

                    Snackbar.make(view, R.string.add_restaurant_failed, Snackbar.LENGTH_SHORT).show()
                }

        } else {
            Snackbar.make(view, R.string.login_required_msg, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun displayAddress(address: String) {
        val addressText = findViewById<MaterialTextView>(R.id.address)
        addressText.text = address
    }

    private fun uploadImage(restaurantName: String) {
        val db = Firebase.firestore
        val user = auth.currentUser

        if (user != null && uri != null) {
            val ref = storageRef.reference.child("Restaurants/$restaurantName")
            ref.putFile(uri!!)
                .addOnSuccessListener {
                    val imageUrl = ref.downloadUrl
                    imageUrl.addOnSuccessListener {
                        imageUri = it.toString()
                        val imgUrl = mapOf("image" to imageUri)

                        db.collection("restaurants")
                            .document(restaurantName)
                            .update(imgUrl)
                            .addOnSuccessListener {
                                Log.d("imageUpload", "Image URL added")
                            }
                            .addOnFailureListener { e ->
                                Log.w("imageUpload", "Error adding URL", e)
                            }
                    }
                }
                .addOnFailureListener {e ->
                    Log.w("imageUpload", "Error uploading image", e)
                }
        }
    }
}