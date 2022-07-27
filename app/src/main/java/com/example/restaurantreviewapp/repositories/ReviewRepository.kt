package com.example.restaurantreviewapp.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.restaurantreviewapp.models.Review
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ReviewRepository {

    private var reviewList: MutableLiveData<List<Review>> = MutableLiveData()

    fun getReviews(restaurantName: String) : LiveData<List<Review>> {
        val db = FirebaseFirestore.getInstance()

        db.collection("restaurants").document(restaurantName).collection("reviews")
            .orderBy("created", Query.Direction.DESCENDING)
            .addSnapshotListener { result, e ->
                if (e != null) {
                    Log.w("tag", "Failed to listen to reviews", e)
                    return@addSnapshotListener
                }
                if (result != null) {
                    reviewList.postValue(result.toObjects(Review::class.java))
                }
            }
        return reviewList
    }
}