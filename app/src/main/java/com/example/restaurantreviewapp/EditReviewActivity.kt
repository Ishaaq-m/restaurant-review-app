package com.example.restaurantreviewapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class EditReviewActivity : AppCompatActivity() {

    private val getImageGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { result: Uri? ->
        val attachedImage = findViewById<ImageView>(R.id.attached_image)
        if (result != null) {
            attachedImage.setImageURI(result)
            uri = result
        }
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                val address = intent?.getStringExtra("address")

                if (address != null) {
                    restaurantAddress = address
                    displayAddress(address)
                }
            }
        }

    private lateinit var storageRef: FirebaseStorage
    private lateinit var ratingBar: RatingBar
    private lateinit var auth: FirebaseAuth
    private lateinit var imageUri: String
    private var restaurantAddress: String = ""
    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_review)

        auth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance()


        val restaurantNameText = findViewById<TextInputEditText>(R.id.restaurant_name_input)
        val reviewText = findViewById<TextInputEditText>(R.id.review_input)
        val addressText = findViewById<MaterialTextView>(R.id.address)
        val attachedImage = findViewById<ImageView>(R.id.attached_image)

        val submitBtn = findViewById<MaterialButton>(R.id.submit_btn)
        val cancelBtn = findViewById<MaterialButton>(R.id.cancel_btn)
        val locationBtn = findViewById<MaterialButton>(R.id.tag_location_btn)
        val imageBtn = findViewById<MaterialButton>(R.id.add_image_btn)

        val intent = intent
        val restaurantName = intent.getStringExtra("restaurantName").toString()
        val review = intent.getStringExtra("review").toString()
        val reviewImage = intent.getStringExtra("image")
        val restaurantRating = intent.getFloatExtra("restaurantRating", 0.0f)
        restaurantAddress = intent.getStringExtra("address").toString()

        ratingBar = findViewById(R.id.rating_bar)

        restaurantNameText.setText(restaurantName)
        ratingBar.rating = restaurantRating
        reviewText.setText(review)
        addressText.text = restaurantAddress

        if (reviewImage != "") {
            Glide.with(this).load(reviewImage).into(attachedImage)
        }

        restaurantNameText.isEnabled = false

        submitBtn.setOnClickListener {
            validateFields(restaurantName, reviewText)
        }

        cancelBtn.setOnClickListener {
            finish()
        }

        locationBtn.setOnClickListener {
            val mapIntent = Intent(this, MapsActivity::class.java)
            startForResult.launch(mapIntent)
        }

        imageBtn.setOnClickListener {
            imagePicker()
        }
    }

    private fun validateFields(restaurantName: String, reviewInput: TextInputEditText) {
        val view = findViewById<ViewGroup>(R.id.add_review)
        val ratingBar = findViewById<RatingBar>(R.id.rating_bar)
        val review = reviewInput.text.toString().trim()
        val rating = ratingBar.rating

        if (review.isEmpty()) {
            reviewInput.error = getString(R.string.review_empty_error)
        }
        else if (review.length < 4) {
            reviewInput.error = getString(R.string.review_length_error)
        }
        else if (rating == 0.0f) {
            Snackbar.make(view, R.string.rating_empty_error, Snackbar.LENGTH_LONG).show()
        }
        else {
            editReview(view, restaurantName, review, rating)
            finish()
        }
    }

    private fun displayAddress(address: String) {
        val addressText = findViewById<MaterialTextView>(R.id.address)
        addressText.text = address
    }

    private fun imagePicker() {
        getImageGallery.launch("image/*")
    }

    private fun editReview(view: View, restaurantName: String, review: String, rating: Float) {
        val db = FirebaseFirestore.getInstance()
        val user = auth.currentUser

        if (user != null) {
            val newReview = mapOf(
                "rating" to rating,
                "review" to review,
                "address" to restaurantAddress,
                "created" to Timestamp(Date()),
            )

            db.collection("restaurants")
                .document(restaurantName)
                .collection("reviews")
                .document(user.uid)
                .update(newReview)
                .addOnSuccessListener {
                    updateImage(restaurantName)

                    Snackbar.make(view, R.string.edit_success_msg, Snackbar.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Snackbar.make(view, R.string.edit_error_msg, Snackbar.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateImage(restaurantName: String) {
        val db = FirebaseFirestore.getInstance()
        val user = auth.currentUser

        if (user != null && uri != null) {
            val ref = storageRef.reference.child(restaurantName + "/" + user.uid)
            ref.putFile(uri!!)
                .addOnSuccessListener {
                    val imageUrl = ref.downloadUrl
                    imageUrl.addOnSuccessListener {
                        imageUri = it.toString()
                        val imgUrl = mapOf("imageURL" to imageUri)

                        db.collection("restaurants")
                            .document(restaurantName)
                            .collection("reviews")
                            .document(user.uid)
                            .update(imgUrl)
                            .addOnSuccessListener {

                                Log.d("imageUpload", "Image URL added")
                            }
                            .addOnFailureListener { e ->
                                Log.w("imageUpload", "Error adding URL", e)
                            }
                    }

                }
                .addOnFailureListener {e ->
                    Log.w("imageUpload", "Error uploading image", e)
                }
        }
    }
}