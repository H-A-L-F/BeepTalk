package com.example.beeptalk.tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beeptalk.databinding.FragmentFavoriteVideosTabBinding
import com.example.beeptalk.lib.PostRVAdapter
import com.example.beeptalk.lib.RecyclerViewInterface
import com.example.beeptalk.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class FavoriteVideosTab(private var userId: String) : Fragment(), RecyclerViewInterface {

    private lateinit var binding: FragmentFavoriteVideosTabBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var posts: ArrayList<Post>
    private lateinit var postRVAdapter: PostRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteVideosTabBinding.inflate(layoutInflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        posts = arrayListOf()
        postRVAdapter = context?.let { PostRVAdapter(it, posts, this) }!!

        binding.postRV.adapter = postRVAdapter
        binding.postRV.layoutManager =
            GridLayoutManager(context, 3, LinearLayoutManager.VERTICAL, false)
        binding.postRV.setHasFixedSize(true)
        getPosts()

        return binding.root
    }

    private fun getPosts() {
        firebaseFirestore.collection("posts").whereArrayContains("favorites", userId)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                firebaseFirestoreException?.let {
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                querySnapshot?.let {
                    posts.clear()
                    for (document in querySnapshot.documents) {
                        var curr = document.toObject(Post::class.java)
                        curr?.id = document.id.toString()
                        curr?.let { it1 -> posts.add(it1) }
                    }

                    postRVAdapter.notifyDataSetChanged()
                }
            }
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }

}