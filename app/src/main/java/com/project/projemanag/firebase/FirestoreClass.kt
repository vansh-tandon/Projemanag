package com.project.projemanag.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.auth.User
import com.project.projemanag.activities.MainActivity
import com.project.projemanag.activities.MyProfileActivity
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

    fun updateUserProfileData(activity: MyProfileActivity,
                              userHashMap: HashMap<String, Any>){
        mFireStore.collection("Users")
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Profile Data update successfully!")
                Toast.makeText(activity, "Profile updated successfully", Toast.LENGTH_LONG).show()
                activity.profileUpdateSuccess()
            }.addOnFailureListener {
                e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
                Toast.makeText(activity, "Error when updating the file", Toast.LENGTH_LONG).show()
            }
    }

    fun loadUserData(activity: Activity){
        mFireStore.collection("Users")
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(com.project.projemanag.models.User::class.java)!!
                when (activity) {
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                    is MyProfileActivity ->{
                        activity.setUserDataInUI(loggedInUser)
                    }

                }
            }
            .addOnFailureListener {

                e-> when (activity) {
                is SignInActivity -> {
                    activity.hideProgressDialog()
                }
                is MainActivity -> {
                    activity.hideProgressDialog()
                }
            }
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