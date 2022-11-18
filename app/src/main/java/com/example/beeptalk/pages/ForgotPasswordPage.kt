package com.example.beeptalk.pages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.beeptalk.databinding.ActivityForgotPasswordPageBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordPage : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgotPasswordPageBinding.inflate(layoutInflater)
        super.setContentView(binding.root)

        binding.sendEmailBtn.setOnClickListener {

            val email = binding.emailET.text.toString()

            if (email.isNotEmpty()) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Email sent, please check your email!",
                                Toast.LENGTH_SHORT
                            ).show()
                            goToLoginPage()
                        } else {
                            Toast.makeText(this, "Email not found!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Please input valid email!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty field are not allowed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToLoginPage() {
        val intent = Intent(this, LoginPage::class.java)
        startActivity(intent)
        finish()
    }

}