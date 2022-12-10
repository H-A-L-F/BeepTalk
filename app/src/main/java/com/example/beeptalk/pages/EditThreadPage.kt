package com.example.beeptalk.pages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.beeptalk.databinding.ActivityEditThreadPageBinding

class EditThreadPage : AppCompatActivity() {

    private lateinit var binding: ActivityEditThreadPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditThreadPageBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}