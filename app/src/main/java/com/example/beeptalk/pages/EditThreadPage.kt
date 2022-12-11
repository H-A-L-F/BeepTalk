package com.example.beeptalk.pages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.beeptalk.databinding.ActivityEditThreadPageBinding
import com.example.beeptalk.parcel.ThreadID
import com.google.firebase.firestore.FirebaseFirestore

class EditThreadPage : AppCompatActivity() {

    private lateinit var binding: ActivityEditThreadPageBinding
    private lateinit var db: FirebaseFirestore

    private lateinit var currThread: ThreadID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditThreadPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currThread = intent.getParcelableExtra("thread")!!

        binding.apply {
            etThreadBody.setText(currThread.body)
        }

        db = FirebaseFirestore.getInstance()

        binding.btnPost.setOnClickListener {
            val body = binding.etThreadBody.text.toString()

            db.collection("threads").document(currThread.id)
                .update("body", body).addOnSuccessListener {
                    binding.etThreadBody.text.clear()

                    Toast.makeText(this, "Edited thread", Toast.LENGTH_SHORT).show()
                    intent = Intent(this, MainPage::class.java)
                    startActivity(intent)
                }
        }

        binding.btnDelete.setOnClickListener {
            db.collection("threads").document(currThread.id)
                .delete().addOnSuccessListener {
                    Toast.makeText(this, "Deleted thread", Toast.LENGTH_SHORT).show()
                    intent = Intent(this, MainPage::class.java)
                    startActivity(intent)
                }
        }
    }
}