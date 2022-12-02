package com.bashirli.mysocial.fragment.logreg

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import com.bashirli.mysocial.R
import com.bashirli.mysocial.activity.ScreenActivity
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text

class LoginFragment : Fragment() {
    private lateinit var animText: TextView
    private lateinit var imageView: ImageView
    private lateinit var goBack:TextView
    private lateinit var  auth:FirebaseAuth
    private lateinit var email:EditText
    private lateinit var pass:EditText
    private lateinit var button:Button
    private lateinit var forgotPass:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authEverything(view)
        mainAnimation()

    }



    private fun authEverything(view:View){
        email=view.findViewById(R.id.editTextTextPersonName3)
        pass=view.findViewById(R.id.editTextTextPassword3)
        button=view.findViewById(R.id.button2)
        forgotPass=view.findViewById(R.id.forgotPass)
        forgotPass.setOnClickListener {
            val navDirections=LoginFragmentDirections.actionLoginFragmentToChangePassLoginFragment()
            Navigation.findNavController(requireActivity(),R.id.fragmentContainerView2).navigate(navDirections)
        }
        button.setOnClickListener{
            login(view)
        }
        imageView=view.findViewById(R.id.animImage)
        animText=view.findViewById(R.id.animTextLogin)
        goBack=view.findViewById(R.id.goBackLogin)
        auth= FirebaseAuth.getInstance()

        imageView.setOnClickListener{
            imageAnimation()
        }
        goBack.setOnClickListener{
            val alertDialog= AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Diqqət!").setMessage("Geri dönməyə əminsiniz?")
                .setNegativeButton("Xeyr"){
                        dialog,which->return@setNegativeButton
                }.setPositiveButton("Bəli"){
                        dialog,which->
                    val navDirections = LoginFragmentDirections.actionLoginFragmentToLogRegFragment()
                    Navigation.findNavController(requireActivity(),R.id.fragmentContainerView2)
                        .navigate(navDirections)
                }.show()
        }

    }

    private fun errorFind():Int{
        if(email.text.toString().equals("")||
                pass.text.toString().equals("")){
            Toast.makeText(requireContext(),"Xana boş buraxılıb",Toast.LENGTH_SHORT).show()
            return 0
        }

        if(pass.text.toString().length<6){
            Toast.makeText(requireContext(),"Şifrə çox qısadır! (Minimum 6 simvol)",Toast.LENGTH_SHORT).show()
            return 0

        }


        return 1
    }


    private fun login(view:View){
    if(errorFind()==0){
        return
    }
        val progressDialog= ProgressDialog(requireContext())
        progressDialog.setTitle("Hesaba giriş")
        progressDialog.setMessage("Zəhmət olmasa gözləyin")
        progressDialog.setCancelable(false)
        progressDialog.show()

        auth.signInWithEmailAndPassword(email.text.toString(),pass.text.toString()).addOnSuccessListener {
            startActivity(Intent(requireActivity(),ScreenActivity::class.java))
            requireActivity().finish()
            progressDialog.cancel()
            Toast.makeText(requireContext(),"Xoş gəldiniz!",Toast.LENGTH_SHORT).show()

        }.addOnFailureListener {
            progressDialog.cancel()
            Toast.makeText(requireContext(),it.localizedMessage,Toast.LENGTH_SHORT).show()

        }


    }

    private fun imageAnimation(){
        val animation=AnimationUtils.loadAnimation(requireContext(),R.anim.anim2)
        imageView.startAnimation(animation)

    }
    private fun mainAnimation(){
        val animation=AnimationUtils.loadAnimation(requireContext(),R.anim.anim3)
        animText.startAnimation(animation)
    }

}