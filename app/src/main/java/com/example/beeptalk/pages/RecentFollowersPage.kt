package com.example.beeptalk.pages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.beeptalk.databinding.ActivityRecentFollowersPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class RecentFollowersPage : AppCompatActivity() {

    private lateinit var binding : ActivityRecentFollowersPageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecentFollowersPageBinding.inflate(layoutInflater)
        super.setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()

    }
}