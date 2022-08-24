package com.example.restaurantreviewapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantreviewapp.models.Restaurant
import com.example.restaurantreviewapp.viewmodels.RestaurantListViewModel
import com.google.firebase.auth.FirebaseAuth


class FavouritesFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerAdapter: RestaurantRecyclerViewAdapter
    private lateinit var restaurantListViewModel: RestaurantListViewModel
    private var favouritesList = ArrayList<Restaurant>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        restaurantListViewModel = ViewModelProvider(this).get(RestaurantListViewModel::class.java)

        (requireActivity() as MainActivity).supportActionBar?.title = getString(R.string.favourites)

        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_favourites, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.favourites_recycler)

        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerAdapter = RestaurantRecyclerViewAdapter(favouritesList)
        recyclerView.adapter = recyclerAdapter

        return view
    }

    override fun onStart() {
        super.onStart()

        restaurantListViewModel.getFavouriteRestaurants().observe(viewLifecycleOwner, { favourites ->
            if (favourites != null) {
                favouritesList.clear()
                favouritesList.addAll(favourites)
                recyclerAdapter.notifyDataSetChanged()
            }
        })
    }
}