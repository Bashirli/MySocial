package com.bashirli.mysocial.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import com.bashirli.mysocial.R
import com.bashirli.mysocial.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        auth= FirebaseAuth.getInstance()

startAnimation()
    val handler=Handler()
        handler.postDelayed({
        if(auth.currentUser!=null){
            startActivity(Intent(this@MainActivity, ScreenActivity::class.java))
            finish()
        }else{
            auth.signInAnonymously()
            startActivity(Intent(this@MainActivity, ScreenActivity::class.java))
            finish()
        }

        },1500)

    }

    private fun startAnimation(){
        var animation=AnimationUtils.loadAnimation(this@MainActivity, R.anim.anim1)
        binding.imageView3.startAnimation(animation)
    }


}