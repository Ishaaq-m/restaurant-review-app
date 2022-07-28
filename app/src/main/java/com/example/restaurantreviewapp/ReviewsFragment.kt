package com.example.restaurantreviewapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantreviewapp.models.Review
import com.example.restaurantreviewapp.viewmodels.ReviewListViewModel
import com.google.firebase.auth.FirebaseAuth

class ReviewsFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerAdapter: ReviewsRecyclerViewAdapter
    private lateinit var userReviewsListViewModel: ReviewListViewModel
    private var userReviewsList = ArrayList<Review>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userReviewsListViewModel = ViewModelProvider(this).get(ReviewListViewModel::class.java)

        (requireActivity() as MainActivity).supportActionBar?.title = getString(R.string.your_reviews_title)
        setHasOptionsMenu(true)

        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_reviews, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.user_reviews_recycler)

        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerAdapter = ReviewsRecyclerViewAdapter(userReviewsList)
        recyclerView.adapter = recyclerAdapter

        return view
    }

    override fun onStart() {
        super.onStart()

        val user = auth.currentUser

        if (user != null) {
            userReviewsListViewModel.getUserReviews(user.uid).observe(viewLifecycleOwner, { reviews ->
                if (reviews != null) {
                    userReviewsList.clear()
                    userReviewsList.addAll(reviews)
                    recyclerAdapter.notifyDataSetChanged()
                }
            })
        }


    }
}