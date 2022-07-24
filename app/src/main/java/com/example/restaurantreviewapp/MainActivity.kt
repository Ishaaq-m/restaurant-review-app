package com.example.restaurantreviewapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mToolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(mToolbar)

        val homeFragment = HomeFragment()
        val reviewFragment = ReviewsFragment()
        val favouritesFragment = FavouritesFragment()
        val profileFragment = ProfileFragment()

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        setFragment(homeFragment)

        //Setting fragment based on which option the user clicks
        bottomNavigation.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> setFragment(homeFragment)
                R.id.reviews -> setFragment(reviewFragment)
                R.id.favourites -> setFragment(favouritesFragment)
                R.id.user -> setFragment(profileFragment)
            }
            true
        }
    }

    private fun setFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout, fragment)
            commit()
        }

}