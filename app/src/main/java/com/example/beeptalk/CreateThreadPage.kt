package com.example.beeptalk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.beeptalk.databinding.ActivityCreateThreadPageBinding
import com.example.beeptalk.databinding.ActivityMainBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class CreateThreadPage : AppCompatActivity() {
    private lateinit var binding : ActivityCreateThreadPageBinding
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateThreadPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPost.setOnClickListener {
            val threadBody = binding.etThreadBody.text.toString()

            val Thread = Thread(threadBody)

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