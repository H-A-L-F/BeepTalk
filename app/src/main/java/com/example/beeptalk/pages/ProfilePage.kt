package com.example.beeptalk.pages

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beeptalk.databinding.ActivityProfilePageBinding
import com.example.beeptalk.lib.PostRVAdapter
import com.example.beeptalk.lib.RecyclerViewInterface
import com.example.beeptalk.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ProfilePage : AppCompatActivity(), RecyclerViewInterface {

    private lateinit var binding: ActivityProfilePageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var posts: ArrayList<Post>
    private lateinit var postRVAdapter: PostRVAdapter
    private lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        sp = getSharedPreferences("current_user", Context.MODE_PRIVATE)
        posts = arrayListOf()
        postRVAdapter = PostRVAdapter(this, posts, this)

        binding.postRV.adapter = postRVAdapter
        binding.postRV.layoutManager = GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false)
        binding.postRV.setHasFixedSize(true)

        val userId = intent.getStringExtra("userId")

        if (userId != null) {
            firebaseFirestore.collection("users").document(userId).get().addOnSuccessListener {
                val data = it.data
                if (data != null) {
                    binding.nameTV.text = data["name"] as String
                    binding.usernameTV.text = data["username"] as String
                    Picasso.get().load(data["profilePicture"] as String)
                        .into(binding.profilePicture)
                    binding.followersCount.text =
                        (data["followers"] as ArrayList<*>?)?.size.toString()
                    binding.followingCount.text =
                        (data["following"] as ArrayList<*>?)?.size.toString()
                }
            }
                .addOnFailureListener {}

        }
        if (userId != null) {
            getPosts(userId)
        }

        Log.d("size", posts.size.toString())
    }


    private fun getPosts(userId: String) {
        firebaseFirestore.collection("posts").whereEqualTo("userId", userId)
            .get().addOnSuccessListener {
                for (document in it.documents) {
                    val curr = document.toObject(Post::class.java)
                    curr?.id = document.id.toString()
                    curr?.let { it1 -> posts.add(it1) }
                }
                postRVAdapter.notifyDataSetChanged()
            }
    }

    override fun onItemClick(position: Int) {
        // go to video page
    }


}