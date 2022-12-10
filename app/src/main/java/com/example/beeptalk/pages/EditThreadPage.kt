package com.example.beeptalk.pages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.beeptalk.databinding.ActivityEditThreadPageBinding
import com.example.beeptalk.parcel.ThreadID

class EditThreadPage : AppCompatActivity() {

    private lateinit var binding: ActivityEditThreadPageBinding

    private lateinit var currThread: ThreadID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditThreadPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currThread = intent.getParcelableExtra("thread")!!

        binding.apply {
            etThreadBody.setText(currThread.body)
        }
    }
}