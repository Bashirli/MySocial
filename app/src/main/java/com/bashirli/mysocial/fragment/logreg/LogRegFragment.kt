package com.bashirli.mysocial.fragment.logreg

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.bashirli.mysocial.R
import com.bashirli.mysocial.activity.ScreenActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LogRegFragment : Fragment() {
    private lateinit var goback : TextView
    private lateinit var login:ConstraintLayout
    private lateinit var register:ConstraintLayout
    private lateinit var auth:FirebaseAuth
    private lateinit var firestore: FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_reg, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        goback=view.findViewById(R.id.goBackLogReg)
        login=view.findViewById(R.id.giris)
        register=view.findViewById(R.id.qeydiyyat)
        auth= FirebaseAuth.getInstance()
        firestore= FirebaseFirestore.getInstance()

setonclick()
    }


    private fun setonclick(){
        goback.setOnClickListener{
           val alert=AlertDialog.Builder(requireContext())
           alert.setTitle("Geri dön").setMessage("Hesaba giriş etmədən, anonim kimi geri dönməyə əminsiniz?")
               .setNegativeButton("Xeyr"){dialog,which->
                   return@setNegativeButton
               }.setPositiveButton("Bəli"){
                       dialog,which->
                   startActivity(Intent(requireActivity(),ScreenActivity::class.java))
                   requireActivity().finish()
                   auth.signInAnonymously()
               }.show()
        }

        login.setOnClickListener {

            val navDirections=LogRegFragmentDirections.actionLogRegFragmentToLoginFragment()
            Navigation.findNavController(it).navigate(navDirections)
        }

        register.setOnClickListener {
        val navDirections=LogRegFragmentDirections.actionLogRegFragmentToRegisterFragment()
            Navigation.findNavController(it).navigate(navDirections)
        }

    }



}