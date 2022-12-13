package com.example.beeptalk.pages

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.beeptalk.R
import com.example.beeptalk.databinding.ActivityLoginPageBinding
import com.example.beeptalk.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore


class LoginPage : AppCompatActivity() {

    private lateinit var binding: ActivityLoginPageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore

    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        sp = getSharedPreferences("current_user", Context.MODE_PRIVATE)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.googleBtn.setOnClickListener {
            googleSignIn()
        }

        binding.signUpTV.setOnClickListener {
            goToRegisterPage()
        }

        binding.logInBtn.setOnClickListener {
            val email = binding.emailET.text.toString()
            val password = binding.passwordET.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val uid = firebaseAuth.currentUser?.uid

                            if (uid != null) {
                                firebaseFirestore.collection("users").document(uid)
                                    .addSnapshotListener { it2, error ->
                                        val data = it2?.data
                                        if (data != null) {
                                            val editor = sp.edit()
                                            editor.putString("uid", uid)
                                            editor.putString("name", data["name"] as String)
                                            editor.putString("email", data["email"] as String)
                                            editor.putString("username", data["username"] as String)
                                            editor.putString(
                                                "profilePicture",
                                                data["profilePicture"] as String
                                            )
                                            editor.putString("bio", data["bio"] as String)

                                            editor.apply()
                                        }
                                    }
                            } else {
                                Toast.makeText(this, "Error occurred!", Toast.LENGTH_SHORT).show()
                            }

                            goToMainPage()
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Please input valid email!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty field are not allowed!", Toast.LENGTH_SHORT).show()
            }

        }

        binding.forgotPassTV.setOnClickListener {
            goToForgotPasswordPage()
        }
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null) {
            val uid = firebaseAuth.currentUser?.uid

            if (uid != null) {
                firebaseFirestore.collection("users").document(uid)
                    .addSnapshotListener { it2, error ->
                        val data = it2?.data
                        if (data != null) {
                            val editor = sp.edit()
                            editor.putString("uid", uid)
                            editor.putString("name", data["name"] as String)
                            editor.putString("email", data["email"] as String)
                            editor.putString("username", data["username"] as String)
                            editor.putString("profilePicture", data["profilePicture"] as String)
                            editor.putString("bio", data["bio"] as String)

                            editor.apply()
                        }
                    }
            }

            goToMainPage()
        }
    }

    private fun goToMainPage() {
        val intent = Intent(this, MainPage::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToRegisterPage() {
        val intent = Intent(this, RegisterPage::class.java)
        startActivity(intent)
    }

    private fun goToForgotPasswordPage() {
        val intent = Intent(this, ForgotPasswordPage::class.java)
        startActivity(intent)
        finish()
    }

    private fun googleSignIn() {
        val googleIntent = googleSignInClient.signInIntent;
        launcher.launch(googleIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result;
            if (account != null) {
                updateUi(account);
            }
        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private fun updateUi(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                firebaseFirestore.collection("users")
                    .document(firebaseAuth.currentUser!!.uid)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            val uid = firebaseAuth.currentUser?.uid

                            if (uid != null) {
                                firebaseFirestore.collection("users").document(uid)
                                    .addSnapshotListener { it2, error ->
                                        val data = it2?.data
                                        if (data != null) {
                                            val editor = sp.edit()
                                            editor.putString("uid", uid)
                                            editor.putString("name", data["name"] as String)
                                            editor.putString("email", data["email"] as String)
                                            editor.putString("username", data["username"] as String)
                                            editor.putString(
                                                "profilePicture",
                                                data["profilePicture"] as String
                                            )
                                            editor.putString("bio", data["bio"] as String)

                                            editor.apply()
                                        }
                                    }
                            } else {
                                Toast.makeText(this, "Error occurred!", Toast.LENGTH_SHORT).show()
                            }
                            return@addSnapshotListener
                        }
                        if (snapshot != null && !snapshot.exists()) {
                            firebaseFirestore.collection("users").count()
                                .get(AggregateSource.SERVER).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val count = task.result.count + 1
                                        val user = User(
                                            firebaseAuth.currentUser!!.uid,
                                            firebaseAuth.currentUser!!.displayName.toString(),
                                            "user$count",
                                            firebaseAuth.currentUser!!.email.toString(),
                                            "https://firebasestorage.googleapis.com/v0/b/beeptalk-35de8.appspot.com/o/User%2FDefault%20Profile%20Picture%2Fcat_user.jpg?alt=media&token=c3aa7ba4-cd6c-44e5-8a8b-71b3dee98a8b"
                                        )
                                        FirebaseFirestore.getInstance().collection("users")
                                            .document(firebaseAuth.currentUser!!.uid)
                                            .set(user)
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    this,
                                                    "Account registered successfully!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }.addOnFailureListener {
                                                Toast.makeText(
                                                    this,
                                                    it.localizedMessage,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                    }
                                }
                        }
                    }
                goToMainPage()
            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

}