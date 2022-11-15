package com.example.beeptalk.pages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.beeptalk.databinding.ActivityLoginPageBinding
import com.example.beeptalk.databinding.ActivityRegisterPageBinding
import com.example.beeptalk.model.USER_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.log

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

            if (name.isNotEmpty() && username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && !checkUsername(
                    username
                )
            ) {
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        saveUserToFireStore(name, username, email, password, it.result.user?.uid.toString())
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
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
        val user: MutableMap<String, String> = HashMap()
        user["name"] = name
        user["username"] = username
        user["email"] = email
        user["password"] = password

        firebaseFirestore.collection(USER_COLLECTION).document(documentId).set(user).addOnSuccessListener {
            Toast.makeText(this, "Account registered successfully!", Toast.LENGTH_SHORT).show()
            goToLoginPage()
        }.addOnFailureListener{
            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkUsername(username: String): Boolean {
        var check = false
//        firebaseFirestore.collection("users").whereEqualTo("username", username).count().get()

        firebaseFirestore.collection(USER_COLLECTION).whereEqualTo("username", username).get()
        return check
    }

}