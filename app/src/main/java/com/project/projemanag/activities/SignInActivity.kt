package com.project.projemanag.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.project.projemanag.R
import com.project.projemanag.databinding.ActivitySignInBinding

class SignInActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding.btnSignIn.setOnClickListener {
            signInRegisteredUser()
        }
        setActionBar()
    }
    private fun setActionBar(){
        setSupportActionBar(binding.toolbarSignInActivity)

        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        binding.toolbarSignInActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun signInRegisteredUser(){
        val email: String = binding.etEmail.text.toString().trim() {it <= ' '}
        val password: String = binding.etPassword.text.toString().trim() {it <= ' '}

       if(validateForm(email, password)){
           showProgressDialog(resources.getString(R.string.please_wait))
           auth.signInWithEmailAndPassword(email, password)
               .addOnCompleteListener(this) { task ->
                   hideProgressDialog()
                   if (task.isSuccessful) {
                       // Sign in success, update UI with the signed-in user's information
                       Log.d("Sign In", "signInWithEmail:success")
                       val user = auth.currentUser
                       startActivity(Intent(this, MainActivity::class.java))

                   } else {
                       // If sign in fails, display a message to the user.
                       Log.w("Sign In", "signInWithEmail:failure", task.exception)
                       Toast.makeText(baseContext, "Authentication failed.",
                           Toast.LENGTH_SHORT).show()

                   }
               }

       }
    }

    private fun validateForm(email: String, password: String): Boolean{
        return when {

            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter email.")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter password.")
                false
            }
            else -> {
                true
            }
        }
    }
}