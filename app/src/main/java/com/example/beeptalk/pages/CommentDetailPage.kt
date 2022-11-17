package com.example.beeptalk.pages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.beeptalk.databinding.ActivityCommentDetailPageBinding
import com.google.firebase.firestore.FirebaseFirestore

class CommentDetailPage : AppCompatActivity() {

    private lateinit var binding : ActivityCommentDetailPageBinding
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentDetailPageBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}