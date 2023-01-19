package com.example.projemanag.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.projemanag.R
import com.example.projemanag.databinding.ActivityBaseBinding
import com.example.projemanag.databinding.DialogProgressBinding

open class BaseActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false

    private lateinit var mProgressDialog: Dialog

    private lateinit var bindingForDialog: DialogProgressBinding

    private lateinit var binding: ActivityBaseBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /**
     * This function is used to show the progress dialog with the title and message to user.
     */
    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)

        bindingForDialog = DialogProgressBinding.inflate(layoutInflater);

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog.setContentView(bindingForDialog.root)

        bindingForDialog.tvProgressText.text = text

        //Start the dialog and display it on screen.
        mProgressDialog.show()
    }

    /**
     * This function is used to dismiss the progress dialog if it is visible to user.
     */
    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }
}