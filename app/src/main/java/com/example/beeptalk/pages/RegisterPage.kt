package com.example.beeptalk.pages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.beeptalk.databinding.ActivityRegisterPageBinding
import com.example.beeptalk.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterPage : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterPageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        binding.logInTV.setOnClickListener {
            goToLoginPage()
        }

        binding.signUpTV.setOnClickListener {
            val name = binding.nameET.text.toString()
            val username = binding.usernameET.text.toString()
            val email = binding.emailET.text.toString()
            val password = binding.passwordET.text.toString()

            if (name.isNotEmpty() && username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {

                firebaseFirestore.collection(USER_COLLECTION)
                    .whereEqualTo(USER_USERNAME_FIELD, username)
                    .get().addOnSuccessListener { res ->
                        if (res.isEmpty) {
                            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    saveUserToFireStore(
                                        name,
                                        username,
                                        email,
                                        password,
                                        task.result.user?.uid.toString()
                                    )
                                } else {
                                    Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(this, "Username taken!", Toast.LENGTH_SHORT).show()
                        }
                    }

            } else {
                Toast.makeText(this, "Empty field are not allowed!", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun goToLoginPage() {
        val intent = Intent(this, LoginPage::class.java)
        startActivity(intent)
    }

    private fun saveUserToFireStore(
        name: String,
        username: String,
        email: String,
        password: String,
        documentId: String
    ) {
        val user = User(name, username, email, password, "")

        firebaseFirestore.collection(USER_COLLECTION).document(documentId).set(user.createHashMap())
            .addOnSuccessListener {
                Toast.makeText(this, "Account registered successfully!", Toast.LENGTH_SHORT).show()
                goToLoginPage()
            }.addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }

}