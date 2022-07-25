package com.example.restaurantreviewapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

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
        val password = passwordInput.text.toString().trim()

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
        val view = findViewById<ViewGroup>(R.id.register_layout)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else {
                    Snackbar.make(view, "Error creating account", Snackbar.LENGTH_SHORT).show()
                }
            }
    }
}