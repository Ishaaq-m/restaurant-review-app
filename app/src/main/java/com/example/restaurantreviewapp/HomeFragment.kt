package com.example.restaurantreviewapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantreviewapp.models.Restaurant
import com.example.restaurantreviewapp.viewmodels.RestaurantListViewModel
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    private lateinit var restaurantAdapter: RestaurantRecyclerViewAdapter
    private var restaurantList = ArrayList<Restaurant>()
    private lateinit var restaurantListViewModel: RestaurantListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        restaurantListViewModel = ViewModelProvider(this).get(RestaurantListViewModel::class.java)

        (requireActivity() as MainActivity).supportActionBar?.title = getString(R.string.home_header)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.restaurant_recycler)
        val searchView = view.findViewById<SearchView>(R.id.search_bar)

        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        restaurantAdapter = RestaurantRecyclerViewAdapter(restaurantList)
        recyclerView.adapter = restaurantAdapter

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                restaurantAdapter.filter.filter(newText)
                return false
            }

        })

        return view
    }

    override fun onStart() {
        super.onStart()

        restaurantListViewModel.getRestaurants().observe(viewLifecycleOwner, { restaurants ->
            if (restaurants != null) {
                restaurantList.clear()
                restaurantList.addAll(restaurants)
                restaurantAdapter.notifyDataSetChanged()
            }
        })
    }
}