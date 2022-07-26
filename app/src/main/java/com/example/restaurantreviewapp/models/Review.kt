package com.example.restaurantreviewapp.models

import java.util.*

data class Review(var created: Date? = null, var address: String = "", var email: String = "", var imageURL: String = "", var rating: Float = 0.0f, var restaurantID: String = "", var review: String = "", var uid: String = "")
