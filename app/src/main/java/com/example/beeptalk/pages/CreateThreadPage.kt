package com.example.beeptalk.pages

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.beeptalk.models.Thread
import com.example.beeptalk.databinding.ActivityCreateThreadPageBinding
import com.example.beeptalk.parcel.ThreadID
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.concurrent.thread

class CreateThreadPage : AppCompatActivity() {
    private lateinit var binding : ActivityCreateThreadPageBinding
    private lateinit var db : FirebaseFirestore
    private lateinit var sp: SharedPreferences

    private var uid: String = "default"
    private var uname: String = "default"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateThreadPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sp = getSharedPreferences("current_user", Context.MODE_PRIVATE)
        uid = sp.getString("uid", "default")!!
        uname = sp.getString("username", "default")!!

        binding.btnPost.setOnClickListener {
            val threadBody = binding.etThreadBody.text.toString()
            val Thread = Thread(body = threadBody, uid = uid)

            db = FirebaseFirestore.getInstance()
            db.collection("threads").add(Thread).addOnSuccessListener {
                binding.etThreadBody.text.clear()

                Toast.makeText(this, "Posted thread", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Post failed", Toast.LENGTH_SHORT).show()
            }

        }

    }
}