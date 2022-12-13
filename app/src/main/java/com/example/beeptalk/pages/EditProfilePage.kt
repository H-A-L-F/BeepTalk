package com.example.beeptalk.pages

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.beeptalk.databinding.ActivityEditProfilePageBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class EditProfilePage : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfilePageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfilePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()

        val user = firebaseAuth.currentUser



        binding.saveBtn.setOnClickListener {
            val name = binding.nameET.text.toString()
            val username = binding.usernameET.text.toString()
            val password = binding.passwordET.text.toString()
            val bio = binding.bioET.text.toString()

            if(name.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty() && bio.isNotEmpty()) {

            }
        }

//        user?.getIdToken(true)
//            ?.addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val idToken = task.result?.token
//                    val decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken)
//                    val currentPasswordHash = decodedToken?.passwordHash
//
//                    // Compare the current password hash with the new password
//                    if (currentPasswordHash == newPasswordHash) {
//                        // The new password is the same as the old password
//                    } else {
//                        // The new password is different than the old password, update it
//                        user.updatePassword(newPassword)
//                            .addOnCompleteListener { task ->
//                                if (task.isSuccessful) {
//                                    // Password updated successfully
//                                } else {
//                                    // Error occurred, check task.getException() for details
//                                }
//                            }
//                    }
//                } else {
//                    // Error occurred, check task.getException() for details
//                }
//            }
    }
}