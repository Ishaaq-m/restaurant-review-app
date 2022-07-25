package com.example.restaurantreviewapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance();

        val registerBtn = findViewById<MaterialButton>(R.id.register_btn)
        val cancelBtn = findViewById<MaterialButton>(R.id.cancel_btn)

        registerBtn.setOnClickListener {
            validateFields()
        }

        cancelBtn.setOnClickListener {
            finish()
        }
    }

    private fun validateFields() {
        val emailInput = findViewById<TextInputEditText>(R.id.email_input)
        val passwordInput = findViewById<TextInputEditText>(R.id.password_input)

        val email = emailInput.text.toString().trim()
        val password = emailInput.text.toString().trim()

        if (email.isEmpty()) {
            emailInput.error = getString(R.string.email_required_msg)
        } else if (password.isEmpty()) {
            passwordInput.error = getString(R.string.password_required_msg)
        } else if (password.length < 6) {
            passwordInput.error = getString(R.string.password_length_msg)
        }
        else {
            register(email, password)
        }

    }

    private fun register(email: String, password: String) {

    }
}