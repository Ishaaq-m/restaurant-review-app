package com.example.restaurantreviewapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.restaurantreviewapp.models.Review
import com.example.restaurantreviewapp.viewmodels.ReviewListViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth

class RestaurantActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerAdapter: ReviewsRecyclerViewAdapter
    private var reviewsList = ArrayList<Review>()
    private lateinit var reviewsListViewModel: ReviewListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)

        reviewsListViewModel = ViewModelProvider(this).get(ReviewListViewModel::class.java)

        auth = FirebaseAuth.getInstance()

        val recyclerView = findViewById<RecyclerView>(R.id.restaurant_reviews_recycler)
        val view = findViewById<ViewGroup>(R.id.restaurant_page)

        val restaurantImage = findViewById<ImageView>(R.id.restaurant_review_venue)
        val restaurantTitle = findViewById<MaterialTextView>(R.id.restaurant_title)
        val restaurantLocation = findViewById<MaterialTextView>(R.id.location)
        val categoryTitle = findViewById<MaterialTextView>(R.id.category_title)

        val addButton = findViewById<MaterialButton>(R.id.add_review)

        val intent = intent
        val restaurantName = intent.getStringExtra("restaurantName").toString()
        val address = intent.getStringExtra("address").toString()
        val image = intent.getStringExtra("image").toString()
        val category = intent.getStringExtra("category").toString()

        Glide.with(restaurantImage).load(image).into(restaurantImage)
        restaurantTitle.text = restaurantName
        restaurantLocation.text = address
        categoryTitle.text = category

        if (restaurantName != "") {
            getReviews(restaurantName)
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
        recyclerAdapter = ReviewsRecyclerViewAdapter(reviewsList)
        recyclerView.adapter = recyclerAdapter
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