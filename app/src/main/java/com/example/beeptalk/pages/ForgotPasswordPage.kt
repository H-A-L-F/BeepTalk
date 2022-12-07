package com.example.beeptalk.pages

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.beeptalk.databinding.ActivityForgotPasswordPageBinding

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
                    sendEmail(email)
//                    buttonSendEmail(email)
                    binding.emailET.text.clear()
                } else {
                    Toast.makeText(this, "Please input valid email!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty field are not allowed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendEmail(stringReceiverEmail: String){

    }

    private fun goToLoginPage() {
        val intent = Intent(this, LoginPage::class.java)
        startActivity(intent)
        finish()
    }

    fun buttonSendEmail(stringReceiverEmail: String) {

    }




}