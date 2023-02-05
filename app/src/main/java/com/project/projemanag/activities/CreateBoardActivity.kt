package com.project.projemanag.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.project.projemanag.R
import com.project.projemanag.databinding.ActivityCreateBoardBinding
import com.project.projemanag.firebase.FirestoreClass
import com.project.projemanag.models.Board
import com.project.projemanag.utils.Constants
import java.io.IOException

class CreateBoardActivity : BaseActivity() {

    private lateinit var binding: ActivityCreateBoardBinding
    private lateinit var mUsername: String

    private var mSelectedImageFileUri: Uri? = null

    private var mBoardImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        if (intent.hasExtra(Constants.NAME)){
            mUsername = intent.getStringExtra(Constants.NAME).toString()
        }


        binding.ivBoardImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                Constants.showImageChooser(this)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }
        binding.btnCreate.setOnClickListener {
            if (mSelectedImageFileUri!= null){
                uploadBoardImage()
            }
            else{
                showProgressDialog(resources.getString(R.string.please_wait))
                createBoard()
            }
        }
    }

    private fun createBoard(){
        val assignedUserArrayList: ArrayList<String> = ArrayList()
        assignedUserArrayList.add(getCurrentUserID())

        val board = Board(
            binding.etBoardName.text.toString(),
            mBoardImageURL,
            mUsername,
            assignedUserArrayList
        )

        FirestoreClass().createBoard(this, board)
    }

    private fun uploadBoardImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        val sRef : StorageReference = FirebaseStorage.getInstance()
            .reference.child("BOARD_IMAGE"
                    + System.currentTimeMillis()
                    + "."
                    + Constants.getFileExtension(this, mSelectedImageFileUri)
            )

        sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot ->
            Log.i("Board Image URL",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri -> Log.e("Downloadable Image URI", uri.toString())

                createBoard()
            }
        }.addOnFailureListener {
                exception ->
            Toast.makeText(
                this@CreateBoardActivity,
                exception.message,
                Toast.LENGTH_LONG)
                .show()

            hideProgressDialog()
        }
    }

    fun boardCreateSuccessfully(){
        hideProgressDialog()
        finish()
    }

    private fun setupActionBar(){
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_create_board_activity)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.create_board_title)
        }
        binding.toolbarCreateBoardActivity.setNavigationOnClickListener {
            onBackPressed()
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
                    .with(this@CreateBoardActivity)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_board_place_holder)
                    .into(findViewById(R.id.iv_board_image))
            }

            catch (e: IOException){
                e.printStackTrace()
            }
        }
    }
}