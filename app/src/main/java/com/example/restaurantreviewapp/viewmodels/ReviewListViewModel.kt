package com.example.restaurantreviewapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.restaurantreviewapp.models.Review
import com.example.restaurantreviewapp.repositories.ReviewRepository

class ReviewListViewModel : ViewModel() {

    private var reviewRepository = ReviewRepository()

    fun getReviews(restaurantName: String) : LiveData<List<Review>> {
        return reviewRepository.getReviews(restaurantName)
    }

}