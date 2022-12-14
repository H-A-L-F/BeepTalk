package com.example.beeptalk.pages

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.beeptalk.databinding.ActivityUpdatePasswordPageBinding
import com.example.beeptalk.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UpdatePasswordPage : AppCompatActivity() {

    private lateinit var binding: ActivityUpdatePasswordPageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdatePasswordPageBinding.inflate(layoutInflater)
        super.setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        var email = intent.getStringExtra("email")

        binding.updatePasswordBtn.setOnClickListener {
            val password = binding.passwordET.text.toString()
            val confPassword = binding.confirmPasswordET.text.toString()

            if (password.isNotEmpty() && confPassword.isNotEmpty()) {
                if (password == confPassword) {
                    firebaseFirestore.collection("users").whereEqualTo("email", email).get()
                        .addOnSuccessListener {
                            for (document in it.documents) {
                                val curr = document.toObject(User::class.java)

                                if (curr != null) {
                                    curr.email?.let { it1 ->
                                        firebaseAuth.signInWithEmailAndPassword(
                                            it1,
                                            curr.password
                                        ).addOnSuccessListener {
                                            firebaseAuth.currentUser?.updatePassword(password)
                                                ?.addOnSuccessListener {
                                                    Toast.makeText(
                                                        this,
                                                        "Password updated successfully!",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            firebaseAuth.currentUser?.uid?.let { it2 ->
                                                firebaseFirestore.collection("users").document(
                                                    it2
                                                ).update("password", password).addOnSuccessListener {

                                                }
                                            }
                                            goToLoginPage()
                                        }
                                    }
                                }
                            }
                        }.addOnFailureListener {
                        }
                } else {
                    Toast.makeText(this, "Password doesn't match!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun goToLoginPage() {
        val intent = Intent(this, LoginPage::class.java)
        startActivity(intent)
        finish()
    }
}