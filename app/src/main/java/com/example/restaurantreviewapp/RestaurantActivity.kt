package com.example.restaurantreviewapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.restaurantreviewapp.models.Restaurant
import com.example.restaurantreviewapp.models.Review
import com.example.restaurantreviewapp.viewmodels.RestaurantViewModel
import com.example.restaurantreviewapp.viewmodels.ReviewListViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RestaurantActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerAdapter: ReviewsRecyclerViewAdapter
    private var reviewsList = ArrayList<Review>()
    private lateinit var reviewsListViewModel: ReviewListViewModel
    private lateinit var restaurantViewModel: RestaurantViewModel
    private var inFavourites = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)

        reviewsListViewModel = ViewModelProvider(this).get(ReviewListViewModel::class.java)
        restaurantViewModel = ViewModelProvider(this).get(RestaurantViewModel::class.java)

        auth = FirebaseAuth.getInstance()

        val recyclerView = findViewById<RecyclerView>(R.id.restaurant_reviews_recycler)
        val view = findViewById<ViewGroup>(R.id.restaurant_page)

        val restaurantImage = findViewById<ImageView>(R.id.restaurant_review_venue)
        val restaurantTitle = findViewById<MaterialTextView>(R.id.restaurant_title)
        val restaurantLocation = findViewById<MaterialTextView>(R.id.location)
        val categoryTitle = findViewById<MaterialTextView>(R.id.category_title)

        val addButton = findViewById<MaterialButton>(R.id.add_review)

        val favouriteBtnActive = findViewById<ImageButton>(R.id.favourite_btn_active)
        val favouriteBtn = findViewById<ImageButton>(R.id.favourite_btn)

        val intent = intent
        val restaurantName = intent.getStringExtra("restaurantName").toString()
        val address = intent.getStringExtra("address").toString()
        val image = intent.getStringExtra("image").toString()
        val category = intent.getStringExtra("category").toString()

        Glide.with(restaurantImage).load(image).into(restaurantImage)
        restaurantTitle.text = restaurantName
        restaurantLocation.text = address
        categoryTitle.text = category

        val restaurant = Restaurant(image, address, restaurantName, category)

        restaurantViewModel.getFavouriteRestaurantByName(restaurantName).observe(this, { result ->
            if (result != null) {
                inFavourites = true
                favouriteBtnActive.visibility = View.VISIBLE
                favouriteBtn.visibility = View.INVISIBLE
            }
            else {
                inFavourites = false
                favouriteBtnActive.visibility = View.INVISIBLE
                favouriteBtn.visibility = View.VISIBLE
            }
        })

        if (restaurantName != "") {
            getReviews(restaurantName)
        }

        favouriteBtn.setOnClickListener {
            toggleFavourite(restaurant)
        }

        favouriteBtnActive.setOnClickListener {
            toggleFavourite(restaurant)
        }

        val user = auth.currentUser

        addButton.setOnClickListener {
            if (user != null) {
                val reviewIntent = Intent(this, AddReviewActivity::class.java)
                reviewIntent.putExtra("restaurantName", restaurantName)
                startActivity(reviewIntent)
            }
            else {
                Snackbar.make(view, R.string.review_login_error, Snackbar.LENGTH_SHORT).show()
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerAdapter = ReviewsRecyclerViewAdapter(reviewsList, "restaurantView")
        recyclerView.adapter = recyclerAdapter
    }

    private fun toggleFavourite(restaurant: Restaurant) {
        val view = findViewById<ViewGroup>(R.id.restaurant_page)

        val user = auth.currentUser
        val db = Firebase.firestore

        val restaurantDetails = hashMapOf(
            "name" to restaurant.name,
            "address" to restaurant.address,
            "image" to restaurant.image,
            "category" to restaurant.category
        )

        if (user != null) {
            if (!inFavourites) {
                db.collection("users").document(user.uid).collection("favourites")
                    .document(restaurant.name)
                    .set(restaurantDetails)
                    .addOnSuccessListener {
                        Log.d("favouritesTest", "Added to favourites")
                        Snackbar.make(view, getString(R.string.added_to_favourites_msg), Snackbar.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Log.w("favouritesTest", "Error adding to favourites")
                    }
            }
            else {
                db.collection("users").document(user.uid).collection("favourites")
                    .document(restaurant.name)
                    .delete()
                    .addOnSuccessListener {
                        Log.d("favouritesTest", "Removed from favourites")
                        Snackbar.make(view, getString(R.string.removed_from_favourites_msg), Snackbar.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Log.w("favouritesTest", "Error removing from favourites")
                    }
            }
        }
        else {
            Snackbar.make(view, getString(R.string.favourite_login_required_msg), Snackbar.LENGTH_LONG).show()
        }
    }

    private fun getReviews(restaurantName: String) {
        reviewsListViewModel.getReviews(restaurantName).observe(this, { reviews ->
            if (reviews != null) {
                reviewsList.clear()
                reviewsList.addAll(reviews)
                recyclerAdapter.notifyDataSetChanged()
            }
        })
    }


}