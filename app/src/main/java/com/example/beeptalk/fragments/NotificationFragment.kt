package com.example.beeptalk.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beeptalk.R
import com.example.beeptalk.databinding.FragmentNotificationBinding
import com.example.beeptalk.lib.NotificationActivitiesRVAdapter
import com.example.beeptalk.lib.NotificationFollowersRVAdapter
import com.example.beeptalk.lib.RecyclerViewInterface
import com.example.beeptalk.models.Notification
import com.example.beeptalk.models.Thread
import com.google.firebase.firestore.FirebaseFirestore

class NotificationFragment : Fragment(), RecyclerViewInterface {

    private lateinit var binding: FragmentNotificationBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var sp: SharedPreferences

    private lateinit var notif_activities: ArrayList<Notification>
    private lateinit var notif_followers: ArrayList<Notification>

    private lateinit var notificationActivitiesRVAdapter: NotificationActivitiesRVAdapter
    private lateinit var notificationFollowersRVAdapter: NotificationFollowersRVAdapter

    private lateinit var uid: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBinding.inflate(layoutInflater, container, false)

        db = FirebaseFirestore.getInstance()
        sp = requireActivity().getSharedPreferences("current_user", Context.MODE_PRIVATE)

        binding.rvNotificationActivities.layoutManager = LinearLayoutManager(context)
        binding.rvNotificationFollowers.layoutManager = LinearLayoutManager(context)
        binding.rvNotificationActivities.setHasFixedSize(true)
        binding.rvNotificationFollowers.setHasFixedSize(true)

        notif_activities = arrayListOf()
        notif_followers = arrayListOf()

        notificationActivitiesRVAdapter = NotificationActivitiesRVAdapter(notif_activities, this)
        notificationFollowersRVAdapter = NotificationFollowersRVAdapter(notif_followers, this)

        binding.rvNotificationActivities.adapter = notificationActivitiesRVAdapter
        binding.rvNotificationFollowers.adapter = notificationFollowersRVAdapter

        uid = sp.getString("uid", "default")!!

        getNotificationActivities(uid)

        return binding.root
    }

    private fun getNotificationActivities(uid: String) {
        db.collection("notifications").document(uid).collection("activities")
            .get().addOnSuccessListener {
                for (document in it.documents) {
                    val curr = document.toObject(Notification::class.java)
                    curr?.id = document.id.toString()
                    curr?.let { it1 -> notif_activities.add(it1) }
                }

                notificationActivitiesRVAdapter.notifyDataSetChanged()
            }
    }

    private fun getNotificationFollowers() {
        db.collection("notifications").document(uid).collection("followers")
            .get().addOnSuccessListener {
                for (document in it.documents) {
                    val curr = document.toObject(Notification::class.java)
                    curr?.id = document.id.toString()
                    curr?.let { it1 -> notif_followers.add(it1) }
                }

                notificationFollowersRVAdapter.notifyDataSetChanged()
            }
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }

}