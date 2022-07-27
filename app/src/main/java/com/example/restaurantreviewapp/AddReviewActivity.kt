package com.example.restaurantreviewapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddReviewActivity : AppCompatActivity() {

    private val getImageGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { result: Uri? ->
        val attachedImage = findViewById<ImageView>(R.id.attached_image)
        if (result != null) {
            attachedImage.setImageURI(result)
            uri = result
        }
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var storageRef: FirebaseStorage
    private lateinit var imageUri: String
    private var uri: Uri? = null
    private var restaurantAddress: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_review)

        auth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance()

        val submitBtn = findViewById<MaterialButton>(R.id.submit_btn)
        val cancelBtn = findViewById<MaterialButton>(R.id.cancel_btn)
        val imageBtn = findViewById<MaterialButton>(R.id.add_image_btn)

        val restaurantIntent = intent
        val restaurantName = restaurantIntent.getStringExtra("restaurantName").toString()

        val restaurantNameInput = findViewById<TextInputEditText>(R.id.restaurant_name_input)
        restaurantNameInput.setText(restaurantName)
        restaurantNameInput.isEnabled = false

        submitBtn.setOnClickListener {
            validateFields(restaurantName)
        }

        cancelBtn.setOnClickListener {
            finish()
        }

        imageBtn.setOnClickListener {
            imagePicker()
        }


    }

    private fun validateFields(restaurantName: String) {
        val reviewInput = findViewById<TextInputEditText>(R.id.review_input)

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
            createReview(view, restaurantName, review, rating)
            finish()
        }
    }

    private fun imagePicker() {
        getImageGallery.launch("image/*")
    }

    private fun createReview(view: View, restaurantName: String, review: String, rating: Float) {
        val db = FirebaseFirestore.getInstance()
        val user = auth.currentUser

        if (user != null) {
            val newReview = hashMapOf(
                "uid" to user.uid,
                "email" to user.email,
                "rating" to rating,
                "review" to review,
                "restaurantID" to restaurantName,
                "address" to restaurantAddress,
                "created" to Timestamp(Date())
            )

            db.collection("restaurants")
                .document(restaurantName)
                .collection("reviews")
                .document(user.uid)
                .set(newReview)
                .addOnSuccessListener {
                    uploadImage(restaurantName)

                    Snackbar.make(view, R.string.review_success_msg, Snackbar.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Snackbar.make(view, R.string.review_failed_msg, Snackbar.LENGTH_SHORT).show()
                }
        }
        else {
            Snackbar.make(view, R.string.review_login_error, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun uploadImage(restaurantName: String) {
        val db = FirebaseFirestore.getInstance()
        val user = auth.currentUser

        if (user != null && uri != null) {
            val ref = storageRef.reference.child(restaurantName + "/" + user.uid)

            Log.d("imageUpload", uri.toString())
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