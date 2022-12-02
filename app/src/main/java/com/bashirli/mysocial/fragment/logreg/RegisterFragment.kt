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
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import com.bashirli.mysocial.R
import com.bashirli.mysocial.activity.ScreenActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text

class RegisterFragment : Fragment() {
    private  lateinit var animText:TextView
    private lateinit var button: Button
    private lateinit var nickname:EditText
    private lateinit var email:EditText
    private lateinit var pass:EditText
    private lateinit var repass:EditText
    private lateinit var goBack:TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    authEverything(view)
    startAnimation()


    }


    private fun authEverything(view:View){
        auth= FirebaseAuth.getInstance()
        firestore= FirebaseFirestore.getInstance()
    goBack=view.findViewById(R.id.goBackReg)
        animText=view.findViewById(R.id.animText)
        button=view.findViewById(R.id.button)
        email=view.findViewById(R.id.editTextTextPersonName)
        nickname=view.findViewById(R.id.editTextTextPersonName2)
        pass=view.findViewById(R.id.editTextTextPassword)
        repass=view.findViewById(R.id.editTextTextPassword2)

        button.setOnClickListener{
        createAcc(view)
        }

        goBack.setOnClickListener{
            val alertDialog=AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Diqqət!").setMessage("Geri dönməyə əminsiniz?")
                .setNegativeButton("Xeyr"){
                    dialog,which->return@setNegativeButton
                }.setPositiveButton("Bəli"){
                dialog,which->
                    val navDirections = RegisterFragmentDirections.actionRegisterFragmentToLogRegFragment()
                    Navigation.findNavController(requireActivity(),R.id.fragmentContainerView2)
                        .navigate(navDirections)
                }.show()
        }

    }

    private fun findError():Int{
    if(email.text.toString().equals("")||
        nickname.text.toString().equals("")||
        pass.text.toString().equals("")||
        repass.text.toString().equals("")){
        Toast.makeText(requireContext(),"Xana boş buraxılıb",Toast.LENGTH_SHORT).show()
        return 0
    }
        if(!pass.text.toString().equals(repass.text.toString())){
            Toast.makeText(requireContext(),"Şifrələr uyğunlaşmır",Toast.LENGTH_SHORT).show()
            return 0
        }
        if(pass.text.toString().length<6){
            Toast.makeText(requireContext(),"Şifrə çox qısadır! (Minimum 6 simvol)",Toast.LENGTH_SHORT).show()
        return 0
        }

        if(nickname.text.toString().length>25){
            Toast.makeText(requireContext(),"Nickname çox uzundur! (Maks 25 Simvol)",Toast.LENGTH_SHORT).show()
            return 0
        }

        return 1
    }


    private fun createAcc(view:View){
    if(findError()==0){
        return
    }

        val progressDialog=ProgressDialog(requireContext())
        progressDialog.setTitle("Hesab yaradılır")
        progressDialog.setMessage("Zəhmət olmasa gözləyin")
        progressDialog.setCancelable(false)
        progressDialog.show()

        auth.createUserWithEmailAndPassword(email.text.toString(),pass.text.toString())
            .addOnSuccessListener {
            val myEmail=email.text.toString()
            val savedPass=pass.text.toString()
            val myNick=nickname.text.toString()
            val bio : String="null"
            val number:String="null"
            val myData=HashMap<String,Any>()
            myData.put("email",myEmail)
            myData.put("myNick",myNick)
            myData.put("bio",bio)
            myData.put("profilePicture","null")
            myData.put("number",number)

            firestore.collection("UserData").document(email.text.toString()).set(myData)

            auth.signInWithEmailAndPassword(myEmail,savedPass).addOnSuccessListener {
                startActivity(Intent(requireActivity(),ScreenActivity::class.java))

                requireActivity().finish()
                Toast.makeText(requireActivity(),"Hesab uğurla yaradıldı!",Toast.LENGTH_SHORT).show()
                progressDialog.cancel()


            }.addOnFailureListener{

                Toast.makeText(requireActivity(),it.localizedMessage,Toast.LENGTH_SHORT).show()
                progressDialog.cancel()
            }


        }.addOnFailureListener{
            Toast.makeText(requireActivity(),it.localizedMessage,Toast.LENGTH_SHORT).show()
            progressDialog.cancel()

        }




    }



    private fun startAnimation(){
        val animation=AnimationUtils.loadAnimation(requireContext(),R.anim.anim3)
        animText.startAnimation(animation)

    }

    override fun onDestroyView() {
        super.onDestroyView()


    }


}