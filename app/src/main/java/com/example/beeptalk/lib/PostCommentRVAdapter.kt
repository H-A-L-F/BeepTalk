package com.example.beeptalk.lib

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beeptalk.databinding.PostCommentCardBinding
import com.example.beeptalk.models.PostComment
import com.example.beeptalk.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class PostCommentRVAdapter(
    private var postComments: ArrayList<PostComment>,
    private val recyclerViewInterface: RecyclerViewInterface,
) : RecyclerView.Adapter<PostCommentRVAdapter.ViewHolder>() {

    class ViewHolder(
        val binding: PostCommentCardBinding,
        val recyclerViewInterface: RecyclerViewInterface
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    recyclerViewInterface.onItemClick(bindingAdapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return PostCommentRVAdapter.ViewHolder(
            PostCommentCardBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            ), recyclerViewInterface
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val postComment = postComments[position]
        val firebaseFirestore = FirebaseFirestore.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()
        val postRef =
            postComment.postId?.let { it1 -> firebaseFirestore.collection("posts").document(it1) }
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        holder.binding.commentsRV.visibility = View.GONE

        holder.binding.createdDateTV.text = postComment.createdAt.toString()
        holder.binding.commentBodyTV.text = postComment.body

        postComment.userId?.let { firebaseFirestore.collection("users").document(it) }
            ?.addSnapshotListener { value, _ ->
                if (value != null) {
                    val user = value.toObject(User::class.java)

                    if (user != null) {
                        holder.binding.usernameTV.text = user.username
                        Picasso.get()
                            .load(user.profilePicture)
                            .into(holder.binding.profilePicture);
                    }

                }

            }

        holder.binding.likeCommentBtn.setOnClickListener {
            if (!postComment.likes.contains(firebaseAuth.currentUser?.uid)) {
                postComment.postId?.let { it1 ->
                    postComment.id?.let { it2 ->
                        firebaseFirestore.collection("posts").document(it1).collection("comments")
                            .document(
                                it2
                            ).update("likes", FieldValue.arrayUnion(firebaseAuth.currentUser?.uid))
                    }
                }

                if (postComment.dislikes.contains(firebaseAuth.currentUser?.uid)) {
                    postComment.postId?.let { it1 ->
                        postComment.id?.let { it2 ->
                            firebaseFirestore.collection("posts").document(it1)
                                .collection("comments").document(
                                    it2
                                ).update(
                                    "dislikes",
                                    FieldValue.arrayRemove(firebaseAuth.currentUser?.uid)
                                )
                        }
                    }
                }
            } else {
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
                            ).update("dislikes", FieldValue.arrayUnion(firebaseAuth.currentUser?.uid))
                    }
                }

                if (postComment.likes.contains(firebaseAuth.currentUser?.uid)) {
                    postComment.postId?.let { it1 ->
                        postComment.id?.let { it2 ->
                            firebaseFirestore.collection("posts").document(it1)
                                .collection("comments").document(
                                    it2
                                ).update(
                                    "likes",
                                    FieldValue.arrayRemove(firebaseAuth.currentUser?.uid)
                                )
                        }
                    }
                }
            } else {
                postComment.postId?.let { it1 ->
                    postComment.id?.let { it2 ->
                        firebaseFirestore.collection("posts").document(it1).collection("comments")
                            .document(
                                it2
                            ).update("dislikes", FieldValue.arrayRemove(firebaseAuth.currentUser?.uid))
                    }
                }
            }
        }

        holder.binding.reply.setOnClickListener {

        }

        holder.binding.viewAllRepliesBtn.setOnClickListener {

        }

    }

    override fun getItemCount(): Int {
        return postComments.size
    }

}