package com.example.beeptalk.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.beeptalk.R
import com.example.beeptalk.databinding.FragmentNotificationBinding
import com.example.beeptalk.models.Notification
import com.google.firebase.firestore.FirebaseFirestore

class NotificationFragment : Fragment() {

    private lateinit var binding: FragmentNotificationBinding
    private lateinit var db: FirebaseFirestore

    private lateinit var notif_activities: ArrayList<Notification>
    private lateinit var notif_followers: ArrayList<Notification>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

}