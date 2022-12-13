package com.example.beeptalk.pages

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beeptalk.databinding.ActivityProfilePageBinding
import com.example.beeptalk.lib.PostRVAdapter
import com.example.beeptalk.lib.RecyclerViewInterface
import com.example.beeptalk.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ProfilePage : AppCompatActivity(), RecyclerViewInterface {

    private lateinit var binding: ActivityProfilePageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var posts: ArrayList<Post>
    private lateinit var postRVAdapter: PostRVAdapter
    private lateinit var sp: SharedPreferences
    private var isThisUser: Boolean = true

    //    private var name: String = ""
//    private var username: String = ""
//    private var profilePicture: String = ""
//    private var bio: String = ""
//    private var following: ArrayList<String> = arrayListOf()
    private var followers: ArrayList<String> = arrayListOf()
    private var btnDisabled: Boolean = true

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
        binding.postRV.layoutManager =
            GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false)
        binding.postRV.setHasFixedSize(true)

        val userId = intent.getStringExtra("userId")

        if (userId != null) {
            firebaseFirestore.collection("users").document(userId).get().addOnSuccessListener {
                val data = it.data
                if (data != null) {
                    followers = data["followers"] as ArrayList<String>

                    binding.nameTV.text = data["name"] as String
                    binding.usernameTV.text = "@" + data["username"] as String
                    Picasso.get().load(data["profilePicture"] as String)
                        .into(binding.profilePicture)
                    binding.bio.text = data["bio"] as String
                    binding.followersCount.text =
                        (data["followers"] as ArrayList<String>).size.toString()
                    binding.followingCount.text =
                        (data["following"] as ArrayList<String>).size.toString()
                    if (firebaseAuth.currentUser?.uid?.let { it1 ->
                            (data["followers"] as ArrayList<String>).contains(
                                it1
                            )
                        } == true) {
                        binding.button.text = "Following"
                    } else if(firebaseAuth.currentUser?.uid?.let { it1 ->
                            (data["followers"] as ArrayList<String>).contains(
                                it1
                            )
                        } == false) {
                        binding.button.text = "Follow"
                    }
                }

                btnDisabled = false
            }
                .addOnFailureListener {}
            getPosts(userId)

            if (userId != firebaseAuth.currentUser?.uid) {
                isThisUser = false
                binding.recentFollowerBtn.visibility = View.GONE
            }

            binding.button.setOnClickListener {

                if (!btnDisabled) {
                    if (isThisUser) {
                        // go to edit profile
                    } else {
                        if (firebaseAuth.currentUser?.let { it1 -> followers.contains(it1.uid) } == true) {
                            binding.button.text = "Following"
                            firebaseFirestore.collection("users").document(userId).update(
                                "followers", FieldValue.arrayRemove(
                                    firebaseAuth.currentUser!!.uid
                                )
                            )
                            followers.remove(firebaseAuth.currentUser!!.uid)
                        } else {
                            binding.button.text = "Follow"
                            firebaseFirestore.collection("users").document(userId).update(
                                "followers", FieldValue.arrayUnion(
                                    firebaseAuth.currentUser!!.uid
                                )
                            )
                            followers.add(firebaseAuth.currentUser!!.uid)

                        }
                    }
                }
            }
        }


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