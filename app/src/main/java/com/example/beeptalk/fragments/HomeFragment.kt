package com.example.beeptalk.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.beeptalk.databinding.FragmentHomeBinding
import com.example.beeptalk.lib.PostVPAdapter
import com.example.beeptalk.lib.RecyclerViewInterface
import com.example.beeptalk.models.Post
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class HomeFragment : Fragment(), RecyclerViewInterface {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var posts: ArrayList<Post>
    private lateinit var postVPAdapter: PostVPAdapter
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        db = FirebaseFirestore.getInstance()
        posts = arrayListOf()

        postVPAdapter = context?.let { PostVPAdapter(it, posts) }!!

        binding.homeViewPager.adapter = postVPAdapter

        binding.homeViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == postVPAdapter.itemCount - 1) {
                    posts.reverse();
                    postVPAdapter.notifyDataSetChanged()
                    binding.homeViewPager.setCurrentItem(0, false)
                }
            }
        })

        getPosts()
        return binding.root
    }

    private fun getPosts() {
        db.collection("posts")
            .get().addOnSuccessListener {
                for (document in it.documents) {
                    val curr = document.toObject(Post::class.java)
                    curr?.id = document.id.toString()
                    curr?.let { it1 -> posts.add(it1) }
                }
                posts.shuffle();
                postVPAdapter.notifyDataSetChanged()
            }

//        db.collection("posts")
//            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
//                firebaseFirestoreException?.let {
//                    return@addSnapshotListener
//                }
//                querySnapshot?.let {
//                    for (document in querySnapshot.documents) {
//                        val curr = document.toObject(Post::class.java)
//                        curr?.id = document.id.toString()
//                        curr?.let { it1 -> posts.add(it1) }
//                    }
//
//                    posts.shuffle();
//                    postRVAdapter.notifyDataSetChanged()
//                }
//            }
    }

    private fun addPosts() {

//        val post =
//            Post("https://www.tiktok.com/@meumilkeu/video/7153240589318884635?is_from_webapp=1&sender_device=pc&web_id=7091612587439998465")
//
//        db = FirebaseFirestore.getInstance()
//        db.collection("posts").add(post).addOnSuccessListener {
//
//        }.addOnFailureListener {
//
//        }
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }

}