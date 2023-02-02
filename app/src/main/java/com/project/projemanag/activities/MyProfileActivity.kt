package com.project.projemanag.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.project.projemanag.R
import com.project.projemanag.databinding.ActivityMyProfileBinding
import com.project.projemanag.firebase.FirestoreClass
import com.project.projemanag.models.User
import com.project.projemanag.utils.Constants
import com.project.projemanag.utils.Constants.READ_STORAGE_PERMISSION_CODE
import com.project.projemanag.utils.Constants.getFileExtension
import java.io.IOException


class MyProfileActivity : BaseActivity() {

//    companion object{
//        private const val READ_STORAGE_PERMISSION_CODE = 1
//        private const val PICK_IMAGE_REQUEST_CODE = 1
//    }

    private var mSelectedImageFileUri : Uri? = null
    private var mProfileImageURL : String = ""
    private lateinit var  mUserDetails: User

    private lateinit var binding: ActivityMyProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        FirestoreClass().loadUserData(this)

        binding.ivProfileUserImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }else{
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE)
            }
        }

        binding.btnUpdate.setOnClickListener {
            if(mSelectedImageFileUri != null){
                uploadUserImage()
            }
            else{
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }
            else{
                Toast.makeText(this, "Oops, you just denied the permission for storage. " +
                        "You can allow it from the settings.",
                    Toast.LENGTH_LONG).show()
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {
            mSelectedImageFileUri = data.data!!

            try {
                Glide
                    .with(this@MyProfileActivity)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(findViewById(R.id.iv_profile_user_image))
            }

            catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    private fun setupActionBar(){
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_my_profile_activity)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile)
        }
        binding.toolbarMyProfileActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun setUserDataInUI(user: User){

        mUserDetails = user

        Glide
            .with(this@MyProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(findViewById(R.id.iv_profile_user_image))

        binding.etName.setText(user.name)
        binding.etEmail.setText(user.email)
        if(user.mobile != 0L){
            binding.etMobile.setText(user.mobile.toString())
        }
    }

    private fun updateUserProfileData() {
        val userHashMap = HashMap<String, Any>()

//        var anyChangesMade = false

        if (mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image) {
            userHashMap[Constants.IMAGE] = mProfileImageURL
//            anyChangesMade = true
        }

        if (binding.etName.text.toString() != mUserDetails.name) {
            userHashMap[Constants.NAME] = binding.etName.text.toString()
//            anyChangesMade = true
        }

        if (binding.etMobile.text.toString() != mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = binding.etMobile.text.toString().toLong()
        }

//        if (anyChangesMade) {
            FirestoreClass().updateUserProfileData(this, userHashMap)
//        }
    }

    private fun uploadUserImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        if(mSelectedImageFileUri != null){
            val sRef : StorageReference = FirebaseStorage.getInstance()
                .reference.child("USER_IMAGE"
                        + System.currentTimeMillis()
                        + "."
                        + getFileExtension(this, mSelectedImageFileUri))

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot ->
                Log.i("Firebase Image URL",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri -> Log.e("Downloadable Image URI", uri.toString())

                    updateUserProfileData()
                }
            }.addOnFailureListener {
                exception ->
                Toast.makeText(
                    this@MyProfileActivity,
                    exception.message,
                    Toast.LENGTH_LONG)
                    .show()

                hideProgressDialog()
            }
        }
    }



    fun profileUpdateSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
}