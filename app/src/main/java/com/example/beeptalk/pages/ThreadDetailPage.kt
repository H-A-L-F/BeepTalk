package com.example.beeptalk.pages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.beeptalk.databinding.ActivityThreadDetailPageBinding
import com.example.beeptalk.models.Thread
import com.example.beeptalk.parcel.ThreadID

class ThreadDetailPage : AppCompatActivity() {

    private lateinit var binding: ActivityThreadDetailPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThreadDetailPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val thread : ThreadID = intent.getParcelableExtra("thread")!!

        binding.tvCreatedAt.text = thread.createdAt.toString()
        binding.tvThreadBody.text = thread.body
        binding.tvTotalVotes.text = (thread.upvote - thread.downvote).toString()
    }
}