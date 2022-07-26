package com.example.restaurantreviewapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.restaurantreviewapp.models.Restaurant
import com.example.restaurantreviewapp.repositories.RestaurantRepository

class RestaurantListViewModel : ViewModel() {

    private var restaurantRepository = RestaurantRepository()

    fun getRestaurants() : LiveData<List<Restaurant>> {
        return restaurantRepository.getRestaurants()
    }

}