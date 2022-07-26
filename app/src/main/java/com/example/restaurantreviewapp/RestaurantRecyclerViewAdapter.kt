package com.example.restaurantreviewapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.restaurantreviewapp.models.Restaurant
import com.google.android.material.textview.MaterialTextView

class RestaurantRecyclerViewAdapter(private val restaurantList: ArrayList<Restaurant>) : RecyclerView.Adapter<RestaurantRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.restaurants_layout, parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = restaurantList[position]

        holder.restaurantTitle.text = info.name
        holder.restaurantLocation.text = info.address
        holder.category.text = info.category
        Glide.with(holder.restaurantImage.context).load(info.image).into(holder.restaurantImage)

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, RestaurantActivity::class.java)
            intent.putExtra("restaurantName", info.name)
            intent.putExtra("address", info.address)
            intent.putExtra("image", info.image)
            intent.putExtra("category", info.category)

            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    inner class ViewHolder(layout: View) : RecyclerView.ViewHolder(layout) {
        val restaurantImage: ImageView = itemView.findViewById(R.id.restaurant_image)
        val restaurantTitle: MaterialTextView = itemView.findViewById(R.id.restaurant_title)
        val restaurantLocation: MaterialTextView = itemView.findViewById(R.id.location_title)
        val category: MaterialTextView = itemView.findViewById(R.id.category_text)
    }

}