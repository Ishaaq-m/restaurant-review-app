package com.example.restaurantreviewapp.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.restaurantreviewapp.models.Restaurant
import com.google.firebase.firestore.FirebaseFirestore

class RestaurantRepository {

    private var restaurantList: MutableLiveData<List<Restaurant>> = MutableLiveData()

    fun getRestaurants() : LiveData<List<Restaurant>> {
        val db = FirebaseFirestore.getInstance()

        db.collection("restaurants")
            .addSnapshotListener { result, e ->
                if (e != null) {
                    Log.w("tag", "Failed to listen to restaurants", e)
                    return@addSnapshotListener
                }
                if (result != null) {
                    restaurantList.postValue(result.toObjects(Restaurant::class.java))
                }
            }
        return restaurantList
    }


}