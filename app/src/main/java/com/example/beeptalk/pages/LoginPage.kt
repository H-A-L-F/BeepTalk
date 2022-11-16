package com.example.beeptalk.pages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.example.beeptalk.databinding.ActivityLoginPageBinding
import com.google.firebase.auth.FirebaseAuth

class LoginPage : AppCompatActivity() {

    private lateinit var binding: ActivityLoginPageBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.signUpTV.setOnClickListener {
            goToRegisterPage()
        }

        binding.logInBtn.setOnClickListener {
            val email = binding.emailET.text.toString()
            val password = binding.passwordET.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        goToHomePage()
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Empty field are not allowed!", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onStart() {
        super.onStart()

        if (firebaseAuth.currentUser != null) {
            goToHomePage()
        }
    }

    private fun goToHomePage() {
//        val intent = Intent(this, LoginPage::class.java)
//        startActivity(intent)
        finish()
        finish()
    }

    private fun goToRegisterPage() {
        val intent = Intent(this, RegisterPage::class.java)
        startActivity(intent)
    }

}