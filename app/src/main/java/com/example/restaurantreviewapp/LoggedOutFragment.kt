package com.example.restaurantreviewapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar

class LoggedOutFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(R.layout.fragment_logged_out, container, false)
        val bottomNavigationBar = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val loginBtn = view.findViewById<MaterialButton>(R.id.login_btn)
        val registerBtn = view.findViewById<MaterialButton>(R.id.register_btn)

        loginBtn.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }

        registerBtn.setOnClickListener {
            val intent = Intent(activity, RegisterActivity::class.java)
            startActivity(intent)
        }

        if (bottomNavigationBar != null) {
            val snackbar = Snackbar.make(bottomNavigationBar, R.string.feature_requires_login, Snackbar.LENGTH_SHORT)
            snackbar.show()
        }

        return view
    }

}