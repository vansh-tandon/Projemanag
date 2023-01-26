package com.project.projemanag.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.project.projemanag.databinding.ActivitySplashBinding
import com.project.projemanag.firebase.FirestoreClass

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val typeface: Typeface =
            Typeface.createFromAsset(assets, "carbon bl.ttf")
        binding.tvAppName.typeface = typeface

        Handler().postDelayed({

            val currentUserId = FirestoreClass().getCurrentUserId()

            if(currentUserId.isNotEmpty()){
                startActivity(Intent(this,  MainActivity::class.java))
            }
            else{
                startActivity(Intent(this, IntroActivity::class.java))
            }
            startActivity(Intent(this, IntroActivity::class.java))
            finish()
        },2500)
    }
}