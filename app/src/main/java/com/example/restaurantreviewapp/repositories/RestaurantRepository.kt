package com.example.restaurantreviewapp.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.restaurantreviewapp.models.Restaurant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RestaurantRepository {

    private var restaurantList: MutableLiveData<List<Restaurant>> = MutableLiveData()
    private var favouritesList: MutableLiveData<List<Restaurant>> = MutableLiveData()
    private var restaurant: MutableLiveData<Restaurant> = MutableLiveData()

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

    fun getFavouriteRestaurants() : LiveData<List<Restaurant>> {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            db.collection("users").document(user.uid).collection("favourites")
                .addSnapshotListener { result, e ->
                    if (e != null) {
                        Log.w("tag", "Failed to listen to favourites", e)
                        return@addSnapshotListener
                    }
                    if (result != null) {
                        favouritesList.postValue(result.toObjects(Restaurant::class.java))
                    }
                }
        }
        return favouritesList
    }

    fun getFavouriteRestaurantByName(restaurantName: String) : LiveData<Restaurant> {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            db.collection("users").document(user.uid).collection("favourites")
                .document(restaurantName)
                .addSnapshotListener { result, e ->
                    if (e != null) {
                        Log.w("tag", "Failed to listen to favourites", e)
                        return@addSnapshotListener
                    }
                    if (result != null) {
                        restaurant.postValue(result.toObject(Restaurant::class.java))
                    }
                }
        }
        return restaurant
    }

}