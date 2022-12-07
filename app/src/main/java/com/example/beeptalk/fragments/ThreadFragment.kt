package com.example.beeptalk.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beeptalk.R
import com.example.beeptalk.databinding.ActivityThreadPageBinding
import com.example.beeptalk.databinding.FragmentThreadBinding
import com.example.beeptalk.lib.RecyclerViewInterface
import com.example.beeptalk.lib.ThreadRVAdapter
import com.example.beeptalk.models.Thread
import com.example.beeptalk.pages.ThreadDetailPage
import com.example.beeptalk.parcel.ThreadID
import com.google.firebase.firestore.FirebaseFirestore

class ThreadFragment : Fragment(), RecyclerViewInterface {

    private lateinit var binding : FragmentThreadBinding
    private lateinit var threads : ArrayList<Thread>
    private lateinit var threadRVAdapter: ThreadRVAdapter
    private lateinit var db : FirebaseFirestore

    private val sp = activity?.getSharedPreferences("current_user", Context.MODE_PRIVATE)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThreadBinding.inflate(layoutInflater, container, false)

        db = FirebaseFirestore.getInstance()

        binding.rvThread.layoutManager = LinearLayoutManager(context)
        binding.rvThread.setHasFixedSize(true)

        threads = arrayListOf()

        val uid = sp?.getString("uid", "default")
        threadRVAdapter = uid?.let { ThreadRVAdapter(threads, this, it) }!!

        binding.rvThread.adapter = threadRVAdapter

//        subscribeThreads()
        getThreads()

        return binding.root
    }

    private fun subscribeThreads() {
        db.collection("threads")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                firebaseFirestoreException?.let {
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                querySnapshot?.let {
                    for (document in querySnapshot.documents) {
                        var curr = document.toObject(Thread::class.java)
                        curr?.id = document.id.toString()
                        curr?.let { it1 -> threads.add(it1) }
                    }

                    threadRVAdapter.notifyDataSetChanged()
                }
            }
    }

    private fun getThreads() {
        db.collection("threads")
            .get().addOnSuccessListener {
                for (document in it.documents) {
                    var curr = document.toObject(Thread::class.java)
                    curr?.id = document.id.toString()
                    curr?.let { it1 -> threads.add(it1) }
                }

                threadRVAdapter.notifyDataSetChanged()
            }
    }

    override fun onItemClick(position: Int) {
        val curr = threads[position]
        val id = curr.id
        val body =  curr.body
        val stitch = curr.stitch
        val upvote = curr.upvote
        val downvote = curr.downvote
        val createdAt = curr.createdAt

        val threadItem: ThreadID = ThreadID(id!!, body!!, stitch, upvote, downvote, createdAt)

        val intent = Intent(context, ThreadDetailPage::class.java)
        intent.putExtra("thread", threadItem)
        startActivity(intent)

    }
}