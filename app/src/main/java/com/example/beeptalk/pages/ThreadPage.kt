package com.example.beeptalk.pages

import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beeptalk.R
import com.example.beeptalk.databinding.ActivityCreateThreadPageBinding
import com.example.beeptalk.databinding.ActivityThreadPageBinding
import com.example.beeptalk.lib.ThreadRVAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList
import com.example.beeptalk.models.Thread

class ThreadPage : AppCompatActivity() {

    private lateinit var binding : ActivityThreadPageBinding
    private lateinit var rv : RecyclerView
    private lateinit var threads : ArrayList<Thread>
    private lateinit var threadRVAdapter: ThreadRVAdapter
    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThreadPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        binding.rvThread.layoutManager = LinearLayoutManager(this)
        binding.rvThread.setHasFixedSize(true)

        threads = arrayListOf()
//        threads.add(Thread("Etiam vitae lacus vel tellus eleifend egestas. Mauris sed justo facilisis, hendrerit felis at, accumsan lorem. Nulla eleifend euismod interdum. Maecenas eu scelerisque nisi. Vivamus nec dolor a purus viverra faucibus sed ac ante. Praesent et nisi aliquet, ultricies ex ut, commodo urna. Mauris et lacus malesuada, dictum felis ac, mattis lorem."))
//        threads.add(Thread("Etiam vitae lacus vel tellus eleifend egestas. Mauris sed justo facilisis, hendrerit felis at, accumsan lorem. Nulla eleifend euismod interdum. Maecenas eu scelerisque nisi. Vivamus nec dolor a purus viverra faucibus sed ac ante. Praesent et nisi aliquet, ultricies ex ut, commodo urna. Mauris et lacus malesuada, dictum felis ac, mattis lorem."))
//        threads.add(Thread("Etiam vitae lacus vel tellus eleifend egestas. Mauris sed justo facilisis, hendrerit felis at, accumsan lorem. Nulla eleifend euismod interdum. Maecenas eu scelerisque nisi. Vivamus nec dolor a purus viverra faucibus sed ac ante. Praesent et nisi aliquet, ultricies ex ut, commodo urna. Mauris et lacus malesuada, dictum felis ac, mattis lorem."))

        threadRVAdapter = ThreadRVAdapter(threads)

        binding.rvThread.adapter = threadRVAdapter

        subscribeThreads()
    }

    private fun subscribeThreads() {
        db.collection("threads")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                firebaseFirestoreException?.let {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                querySnapshot?.let {
                    for (document in querySnapshot.documents) {
                        document.toObject(Thread::class.java)?.let { it1 -> threads.add(it1) }
                    }

                    threadRVAdapter.notifyDataSetChanged()
                }
            }
    }
}