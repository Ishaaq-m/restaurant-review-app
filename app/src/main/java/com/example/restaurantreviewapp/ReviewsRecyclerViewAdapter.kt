package com.example.restaurantreviewapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.restaurantreviewapp.models.Review
import com.google.android.material.textview.MaterialTextView

class ReviewsRecyclerViewAdapter(private val userReviewsList: ArrayList<Review>) : RecyclerView.Adapter<ReviewsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.user_reviews_layout, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = userReviewsList[position]

        holder.restaurantName.text = info.restaurantID
        holder.restaurantRating.rating = info.rating
        holder.restaurantAddress.text = info.address
        holder.userReview.text = info.review
        holder.timeCreated.text = info.created.toString()
        Glide.with(holder.foodImage.context).load(info.imageURL).into(holder.foodImage)

    }

    override fun getItemCount(): Int {
        return userReviewsList.size
    }

    inner class ViewHolder(layout: View) : RecyclerView.ViewHolder(layout) {
        var restaurantName: MaterialTextView = itemView.findViewById(R.id.restaurant_name)
        var restaurantRating: RatingBar = itemView.findViewById(R.id.rating_bar)
        var restaurantAddress: MaterialTextView = itemView.findViewById(R.id.restaurant_address)
        var userReview: MaterialTextView = itemView.findViewById(R.id.user_review)
        var timeCreated: MaterialTextView = itemView.findViewById(R.id.time_created)
        var foodImage: ImageView = itemView.findViewById(R.id.food_img)
    }

}