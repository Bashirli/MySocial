package com.bashirli.mysocial.activity

import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import com.bashirli.mysocial.R
import com.bashirli.mysocial.databinding.ActivityScreenBinding
import com.bashirli.mysocial.fragment.*
import com.bashirli.mysocial.fragment.profile.MyProfileFragmentDirections
import com.bashirli.mysocial.fragment.profile.ProfileFragmentDirections
import com.bashirli.mysocial.fragment.profile.ProfilePassChangeFragmentDirections
import com.bashirli.mysocial.fragment.profile.SettingsFragmentDirections
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.tasks.Task
import com.google.firebase.auth.FirebaseAuth

class ScreenActivity : AppCompatActivity() {
    private lateinit var binding:ActivityScreenBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityScreenBinding.inflate(layoutInflater)
        var view=binding.root
        setContentView(view)
        auth= FirebaseAuth.getInstance()

        setOnClicks()

    }

    private fun setOnClicks(){
        binding.activityFeedButton.setOnClickListener{
            if(auth.currentUser!!.isAnonymous){
                Snackbar.make(it,"Hal hazırda anonim şəkildəsiz. Hesaba giriş edin ki," +
                        " bu funksiyanı istifadə" +
                        " edə biləsiniz.",Snackbar.LENGTH_SHORT).show()
            return@setOnClickListener
            }else{
               turnToMain()
                val navDirections=FeedFragmentDirections.actionFeedFragmentToActivityFragment()
                Navigation.findNavController(this@ScreenActivity,R.id.fragmentContainerView).navigate(navDirections)

            }
        }


        binding.feedButton.setOnClickListener{
        turnToMain()
        }

        binding.floatingButton.setOnClickListener{
            if(auth.currentUser!!.isAnonymous){
                Snackbar.make(it,"Hal hazırda anonim şəkildəsiz. Hesaba giriş edin ki," +
                        " post paylaşa" +
                        " biləsiniz.",Snackbar.LENGTH_SHORT).show()
            return@setOnClickListener
            }else{
                turnToMain()
                val action=FeedFragmentDirections.actionFeedFragmentToAddPostFragment()
                Navigation.findNavController(this@ScreenActivity,R.id.fragmentContainerView).navigate(action)
            }
        }

        binding.myProfileButton.setOnClickListener{
        if(auth.currentUser!!.isAnonymous){
            val alert=AlertDialog.Builder(this@ScreenActivity)
            alert.setTitle("Hesab").setMessage("Hal hazırda hesabsız istifadə edirsiniz. Hesaba giriş etmək/yaratmaq istəyirsiniz?")
                .setNegativeButton("Xeyr"){dialog,which->
                    return@setNegativeButton
                }.setPositiveButton("Bəli"){
                    dialog,which->
                startActivity(Intent(this@ScreenActivity,LogRegActivity::class.java))
                    finish()

                }.create().show()

        }else{
            turnToMain()
    val navDirections=FeedFragmentDirections.actionFeedFragmentToProfileFragment()
            Navigation.findNavController(this@ScreenActivity, R.id.fragmentContainerView).navigate(navDirections)
        }

        }

        binding.searchButton.setOnClickListener{
       turnToMain()
            val navDirections=FeedFragmentDirections.actionFeedFragmentToSearchFragment()
            Navigation.findNavController(this@ScreenActivity,R.id.fragmentContainerView).navigate(navDirections)

        }

    }


    private fun turnToMain(){
        try {
            try {
                val navDirections=ProfileFragmentDirections.actionProfileFragmentToFeedFragment()
                Navigation.findNavController(this@ScreenActivity,R.id.fragmentContainerView).navigate(navDirections)

            }catch (e:Exception){ }
            try {
                val navDirections=SettingsFragmentDirections.actionSettingsFragmentToFeedFragment()
                Navigation.findNavController(this@ScreenActivity,R.id.fragmentContainerView).navigate(navDirections)

            }catch (e:Exception){ }

            try {

                    val navDirections=AddPostFragmentDirections.actionAddPostFragmentToFeedFragment()
                    Navigation.findNavController(this@ScreenActivity,R.id.fragmentContainerView).navigate(navDirections)



        }catch (e:Exception){}
            try {

                val navDirections=ActivityFragmentDirections.actionActivityFragmentToFeedFragment()
                Navigation.findNavController(this@ScreenActivity,R.id.fragmentContainerView).navigate(navDirections)



            }catch (e:Exception){}

            try {

                        val navDirections=MyProfileFragmentDirections.actionMyProfileFragmentToFeedFragment()
                        Navigation.findNavController(this@ScreenActivity,R.id.fragmentContainerView).navigate(navDirections)


            }catch (e:Exception){}
            try {

                val navDirections=SearchFragmentDirections.actionSearchFragmentToFeedFragment()
                Navigation.findNavController(this@ScreenActivity,R.id.fragmentContainerView).navigate(navDirections)


            }catch (e:Exception){}
            try {

                val navDirections=ProfilePassChangeFragmentDirections.actionProfilePassChangeFragmentToFeedFragment()
                Navigation.findNavController(this@ScreenActivity,R.id.fragmentContainerView).navigate(navDirections)


            }catch (e:Exception){}

        }catch (e:Exception){

        }


    }


}