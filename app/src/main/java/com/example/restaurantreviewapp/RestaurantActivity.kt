package com.example.restaurantreviewapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.restaurantreviewapp.models.Review
import com.google.android.material.textview.MaterialTextView

class RestaurantActivity : AppCompatActivity() {

    private lateinit var recyclerAdapter: ReviewsRecyclerViewAdapter
    private var reviewsList = ArrayList<Review>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)

        val recyclerView = findViewById<RecyclerView>(R.id.restaurant_reviews_recycler)

        val restaurantImage = findViewById<ImageView>(R.id.restaurant_review_venue)
        val restaurantTitle = findViewById<MaterialTextView>(R.id.restaurant_title)
        val restaurantLocation = findViewById<MaterialTextView>(R.id.location)
        val categoryTitle = findViewById<MaterialTextView>(R.id.category_title)

        val intent = intent
        val restaurantName = intent.getStringExtra("restaurantName").toString()
        val address = intent.getStringExtra("address").toString()
        val image = intent.getStringExtra("image").toString()
        val category = intent.getStringExtra("category").toString()

        Glide.with(restaurantImage).load(image).into(restaurantImage)
        restaurantTitle.text = restaurantName
        restaurantLocation.text = address
        categoryTitle.text = category

        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerAdapter = ReviewsRecyclerViewAdapter(reviewsList)
        recyclerView.adapter = recyclerAdapter
    }
}