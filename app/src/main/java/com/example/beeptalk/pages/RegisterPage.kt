package com.example.beeptalk.pages

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.beeptalk.R
import com.example.beeptalk.databinding.ActivityRegisterPageBinding
import com.example.beeptalk.models.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore

class RegisterPage : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterPageBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore

    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var sp : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        sp = getSharedPreferences("current_user", Context.MODE_PRIVATE)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.logInTV.setOnClickListener {
            goToLoginPage()
        }

        binding.googleBtn.setOnClickListener {
            googleSignIn()
        }

        binding.signUpTV.setOnClickListener {
            val name = binding.nameET.text.toString()
            val username = binding.usernameET.text.toString()
            val email = binding.emailET.text.toString()
            val password = binding.passwordET.text.toString()

            if (name.isNotEmpty() && username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    firebaseFirestore.collection(USER_COLLECTION)
                        .whereEqualTo(USER_USERNAME_FIELD, username)
                        .get().addOnSuccessListener { res ->
                            if (res.isEmpty) {
                                firebaseAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            saveUserToFireStore(
                                                name,
                                                username,
                                                email,
                                                task.result.user?.uid.toString(),
                                                this
                                            )
                                            goToLoginPage()
                                        } else {
                                            Toast.makeText(
                                                this,
                                                task.exception.toString(),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(this, "Username taken!", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Please input valid email!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty field are not allowed!", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun goToMainPage() {
        val intent = Intent(this, MainPage::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToLoginPage() {
        val intent = Intent(this, LoginPage::class.java)
        startActivity(intent)
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
                firebaseFirestore.collection(USER_COLLECTION)
                    .document(firebaseAuth.currentUser!!.uid)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            val uid = firebaseAuth.currentUser?.uid

                            if (uid != null) {
                                firebaseFirestore.collection(USER_COLLECTION).document(uid).get().addOnSuccessListener { it2 ->
                                    val editor = sp.edit()
                                    editor.putString("uid", uid)
                                    editor.putString("name", it2.getString(USER_NAME_FIELD))
                                    editor.putString("email", it2.getString(USER_EMAIL_FIELD))
                                    editor.putString("username", it2.getString(USER_USERNAME_FIELD))
                                    editor.putString("profilePicture", it2.getString(
                                        USER_PROFILE_PICTURE_FIELD))

                                    editor.apply()
                                }
                            } else {
                                Toast.makeText(this, "Error occurred!", Toast.LENGTH_SHORT).show()
                            }
                            return@addSnapshotListener
                        }
                        if (snapshot != null && !snapshot.exists()) {
                            firebaseFirestore.collection(USER_COLLECTION).count()
                                .get(AggregateSource.SERVER).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val count = task.result.count + 1
                                        saveUserToFireStore(
                                            firebaseAuth.currentUser!!.displayName.toString(),
                                            "user$count",
                                            firebaseAuth.currentUser!!.email.toString(),
                                            firebaseAuth.currentUser!!.uid,
                                            this
                                        )
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