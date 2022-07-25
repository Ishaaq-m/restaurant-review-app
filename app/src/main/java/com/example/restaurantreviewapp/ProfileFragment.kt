package com.example.restaurantreviewapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (requireActivity() as MainActivity).supportActionBar?.title = getString(R.string.account)

        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val emailText = view.findViewById<MaterialTextView>(R.id.user_email)

        val logoutBtn = view.findViewById<MaterialButton>(R.id.logout_btn)

        logoutBtn.setOnClickListener {
            logout()
        }

        val user = auth.currentUser

        if (user != null) {
            emailText.text = user.email
        }
        else {
            val supportFragment = LoggedOutFragment()
            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(R.id.frame_layout, supportFragment)
                commit()
            }
        }

        return view
    }

    private fun logout() {
        auth.signOut()

        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }


}