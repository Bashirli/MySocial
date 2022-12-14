package com.bashirli.mysocial.fragment.profile

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import com.bashirli.mysocial.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfilePassChangeFragment : Fragment() {
    private lateinit var email: EditText
    private lateinit var button: TextView
    private lateinit var goBack: TextView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_pass_change, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        email=view.findViewById(R.id.editTextTextEmailAddress2)
        button=view.findViewById(R.id.textView17)
        goBack=view.findViewById(R.id.goBackChange)
        firestore= FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()
        email.setText(auth.currentUser!!.email!!)
        goBack.setOnClickListener {
            val navDirections= ProfilePassChangeFragmentDirections.actionProfilePassChangeFragmentToSettingsFragment()
            Navigation.findNavController(requireActivity(),R.id.fragmentContainerView).navigate(navDirections)
        }

        button.setOnClickListener {
            conti(it)
        }

    }

    private fun conti(view:View){
        if(email.text.toString().equals("")){
            Toast.makeText(requireContext(),"Email bo?? burax??l??b", Toast.LENGTH_SHORT).show()
            return
        }

        val progressDialog= ProgressDialog(requireContext())
        progressDialog.setTitle("Yoxlan??l??r")
        progressDialog.setMessage("Z??hm??t olmasa g??zl??yin")
        progressDialog.setCancelable(false)
        progressDialog.show()

        auth.sendPasswordResetEmail(email.text.toString()).addOnSuccessListener {
            progressDialog.cancel()
            val alertDialog= AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Diqq??t!")
                .setMessage("Kodu he?? kiml?? payla??may??n! Kod email hesab??n??za g??nd??rildi. Z??hm??t olmasa oradak?? link?? basaraq ??ifr??nizi d??yi??in." +
                        "Email?? g??nd??rilmi?? kodu tapmasan??z emailin spam b??lm??sin?? bax??n.")
                .setCancelable(false)
                .setPositiveButton("Ok"){
                        dialog,which->
                    val navDirections= ProfilePassChangeFragmentDirections.actionProfilePassChangeFragmentToSettingsFragment()
                    Navigation.findNavController(requireActivity(),R.id.fragmentContainerView).navigate(navDirections)

                }.show()
        }.addOnFailureListener {
            Toast.makeText(requireContext(),it.localizedMessage, Toast.LENGTH_SHORT).show()
            progressDialog.cancel()
        }

    }


}