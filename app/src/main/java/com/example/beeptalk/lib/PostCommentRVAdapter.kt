package com.example.beeptalk.lib

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beeptalk.R
import com.example.beeptalk.databinding.ActivityPostCommentPageBinding
import com.example.beeptalk.databinding.PostCommentCardBinding
import com.example.beeptalk.models.PostComment
import com.example.beeptalk.models.PostCommentReply
import com.example.beeptalk.models.User
import com.example.beeptalk.pages.ProfilePage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class PostCommentRVAdapter(
    private var context: Context,
    private var parentBinding: ActivityPostCommentPageBinding,
    private var postComments: ArrayList<PostComment>,
) : RecyclerView.Adapter<PostCommentRVAdapter.ViewHolder>() {

    class ViewHolder(
        val binding: PostCommentCardBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            PostCommentCardBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val postComment = postComments[position]
        val firebaseFirestore = FirebaseFirestore.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()

        holder.binding.repliesRV.visibility = View.GONE
        holder.binding.createdDateTV.text = postComment.createdAt.toString()
        holder.binding.commentBodyTV.text = postComment.body

        postComment.userId?.let { firebaseFirestore.collection("users").document(it) }
            ?.addSnapshotListener { value, _ ->
                if (value != null) {
                    val user = value.toObject(User::class.java)

                    if (user != null) {
                        holder.binding.usernameTV.text = "@" + user.username
                        Picasso.get()
                            .load(user.profilePicture)
                            .into(holder.binding.profilePicture);

                        holder.binding.reply.setOnClickListener {
                            parentBinding.usernameReplyToTV.text = user.username
                            parentBinding.replyingToLayout.visibility = View.VISIBLE
                        }

                        holder.binding.profilePicture.setOnClickListener {
                            goToProfilePage(postComment.userId)
                        }

                        holder.binding.usernameTV.setOnClickListener {
                            goToProfilePage(postComment.userId)
                        }
                    }

                }

            }

        if (postComment.likes.contains(firebaseAuth.currentUser?.uid)) {
            holder.binding.likeCommentBtn.setImageResource(R.drawable.ic_baseline_like_filled_24)
        }

        if (postComment.dislikes.contains(firebaseAuth.currentUser?.uid)) {
            holder.binding.dislikeCommentBtn.setImageResource(R.drawable.ic_baseline_thumb_down_24)
        }

        holder.binding.likeCommentBtn.setOnClickListener {
            if (!postComment.likes.contains(firebaseAuth.currentUser?.uid)) {
                postComment.postId?.let { it1 ->
                    postComment.id?.let { it2 ->
                        firebaseFirestore.collection("posts").document(it1).collection("comments")
                            .document(
                                it2
                            ).update("likes", FieldValue.arrayUnion(firebaseAuth.currentUser?.uid))
                            .addOnSuccessListener {
                                holder.binding.likeCommentBtn.setImageResource(R.drawable.ic_baseline_like_filled_24)
                                if (postComment.dislikes.contains(firebaseAuth.currentUser?.uid)) {
                                    postComment.postId.let { it1 ->
                                        postComment.id?.let { it2 ->
                                            firebaseFirestore.collection("posts").document(it1)
                                                .collection("comments").document(
                                                    it2
                                                ).update(
                                                    "dislikes",
                                                    FieldValue.arrayRemove(firebaseAuth.currentUser?.uid)
                                                ).addOnSuccessListener {
                                                    holder.binding.dislikeCommentBtn.setImageResource(
                                                        R.drawable.ic_outline_thumb_down_24
                                                    )
                                                }
                                        }
                                    }
                                }
                            }
                    }
                }

            } else {
                holder.binding.likeCommentBtn.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                postComment.postId?.let { it1 ->
                    postComment.id?.let { it2 ->
                        firebaseFirestore.collection("posts").document(it1).collection("comments")
                            .document(
                                it2
                            ).update("likes", FieldValue.arrayRemove(firebaseAuth.currentUser?.uid))
                    }
                }
            }
        }

        holder.binding.dislikeCommentBtn.setOnClickListener {
            if (!postComment.dislikes.contains(firebaseAuth.currentUser?.uid)) {

                postComment.postId?.let { it1 ->
                    postComment.id?.let { it2 ->
                        firebaseFirestore.collection("posts").document(it1).collection("comments")
                            .document(
                                it2
                            ).update(
                                "dislikes",
                                FieldValue.arrayUnion(firebaseAuth.currentUser?.uid)
                            ).addOnSuccessListener {
                                holder.binding.dislikeCommentBtn.setImageResource(R.drawable.ic_baseline_thumb_down_24)

                                if (postComment.likes.contains(firebaseAuth.currentUser?.uid)) {
                                    postComment.postId.let { it1 ->
                                        postComment.id?.let { it2 ->
                                            firebaseFirestore.collection("posts").document(it1)
                                                .collection("comments").document(
                                                    it2
                                                ).update(
                                                    "likes",
                                                    FieldValue.arrayRemove(firebaseAuth.currentUser?.uid)
                                                ).addOnSuccessListener {
                                                    holder.binding.likeCommentBtn.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                                                }
                                        }
                                    }
                                }
                            }
                    }
                }


            } else {
                holder.binding.dislikeCommentBtn.setImageResource(R.drawable.ic_outline_thumb_down_24)
                postComment.postId?.let { it1 ->
                    postComment.id?.let { it2 ->
                        firebaseFirestore.collection("posts").document(it1).collection("comments")
                            .document(
                                it2
                            ).update(
                                "dislikes",
                                FieldValue.arrayRemove(firebaseAuth.currentUser?.uid)
                            )
                    }
                }
            }
        }

        holder.binding.viewAllRepliesBtn.setOnClickListener {
            holder.binding.repliesRV.visibility = View.VISIBLE
            holder.binding.viewAllRepliesBtn.visibility = View.GONE

            val postCommentReplies : ArrayList<PostCommentReply> = arrayListOf()

            val postCommentReplyRVAdapter = postComment.postId?.let { it1 ->
                PostCommentReplyRVAdapter(context, holder.binding,
                    it1, postCommentReplies)
            }
            holder.binding.repliesRV.layoutManager = LinearLayoutManager(context)
            holder.binding.repliesRV.setHasFixedSize(true)
            holder.binding.repliesRV.adapter = postCommentReplyRVAdapter

            postComment.postId?.let { it1 -> postComment.userId?.let { it2 ->
                if (postCommentReplyRVAdapter != null) {
                    getAllReplies(it1,
                        it2, postCommentReplies, postCommentReplyRVAdapter
                    )
                }
            } }
        }

    }

    override fun getItemCount(): Int {
        return postComments.size
    }

    private fun goToProfilePage(userId: String) {
        val intent = Intent(context, ProfilePage::class.java)
        intent.putExtra("userId", userId)
        context.startActivity(intent)
    }

    private fun getAllReplies(postId: String, commentId: String, postCommentReplies: ArrayList<PostCommentReply>, postCommentReplyRVAdapter: PostCommentReplyRVAdapter) {
        FirebaseFirestore.getInstance().collection("posts").document(postId).collection("comments")
            .document(commentId).collection("reply").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                querySnapshot?.let {
                    postCommentReplies.clear()
                    for (document in querySnapshot.documents) {
                        val curr = document.toObject(PostCommentReply::class.java)
                        curr?.id = document.id.toString()
                        curr?.let { it1 -> postCommentReplies.add(it1) }
                    }
                    postCommentReplyRVAdapter.notifyDataSetChanged()
                }
        }
    }

}