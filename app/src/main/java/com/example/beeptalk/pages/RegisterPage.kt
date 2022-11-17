package com.example.beeptalk.pages

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.beeptalk.R
import com.example.beeptalk.databinding.ActivityRegisterPageBinding
import com.example.beeptalk.model.*
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.logInTV.setOnClickListener {
            goToLoginPage()
        }

        binding.signUpTV.setOnClickListener {
            val name = binding.nameET.text.toString()
            val username = binding.usernameET.text.toString()
            val email = binding.emailET.text.toString()
            val password = binding.passwordET.text.toString()

            if (name.isNotEmpty() && username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
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
                Toast.makeText(this, "Empty field are not allowed!", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun goToHomePage() {
        val intent = Intent(this, HomePage::class.java)
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
                goToHomePage()
            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

}