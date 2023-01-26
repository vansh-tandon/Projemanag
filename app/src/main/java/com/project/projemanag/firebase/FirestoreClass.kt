package com.project.projemanag.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.auth.User
import com.project.projemanag.activities.SignInActivity
import com.project.projemanag.activities.SignUpActivity

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: com.project.projemanag.models.User){
        Log.d("userDetails",userInfo.toString()+"33")
        mFireStore.collection("Users")
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("successs",it.toString())
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener {
                Log.d("failured",it.toString())
            }

    }

    fun signInUser(activity: SignInActivity){
        mFireStore.collection("Users")
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)
                if (loggedInUser != null) {
                    activity.signInSuccess(loggedInUser)
                }
            }
            .addOnFailureListener {
                e->
            Log.e("SignInUser","Error writing document", e)
            }
    }
    fun getCurrentUserId(): String{
        val currentUser =FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if(currentUser != null){
            currentUserId = currentUser.uid
        }
        return currentUserId
    }
}