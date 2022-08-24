package com.example.restaurantreviewapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.restaurantreviewapp.models.Restaurant
import com.example.restaurantreviewapp.repositories.RestaurantRepository

class RestaurantViewModel : ViewModel() {

    private var restaurantRepository = RestaurantRepository()

    fun getFavouriteRestaurantByName(restaurantName: String) : LiveData<Restaurant> {
        return restaurantRepository.getFavouriteRestaurantByName(restaurantName)
    }

}