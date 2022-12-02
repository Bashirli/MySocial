package com.bashirli.mysocial.fragment.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import com.bashirli.mysocial.R
import com.bashirli.mysocial.activity.LogRegActivity
import com.google.firebase.auth.FirebaseAuth

class SettingsFragment : Fragment() {
    private lateinit var lin1:LinearLayout
    private lateinit var lin2:LinearLayout
    private lateinit var lin3:LinearLayout
    private lateinit var hesabAyar:TextView
    private lateinit var proqAyar:TextView
    private lateinit var exit:TextView
    private lateinit var goBack:TextView
    private lateinit var myProfile:TextView
    private lateinit var auth:FirebaseAuth
    private lateinit var changePass:TextView
    private lateinit var closeApp:TextView
    private lateinit var destekleElaqe:TextView
    private lateinit var profilPaylas:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    authEverything(view)
    }


    private fun authEverything(view: View){
        lin1=view.findViewById(R.id.hesabAyarLinear)
        lin2=view.findViewById(R.id.proqramAyarLinear)
        lin3=view.findViewById(R.id.exitLinear)
        hesabAyar=view.findViewById(R.id.hesabAyar)
        proqAyar=view.findViewById(R.id.proqramSettings)
        changePass=view.findViewById(R.id.changePass)
        exit=view.findViewById(R.id.exit)
        goBack=view.findViewById(R.id.goBackSettings)
        profilPaylas=view.findViewById(R.id.publishMyProfile)
        destekleElaqe=view.findViewById(R.id.destekXetti)
        closeApp=view.findViewById(R.id.closeApp)
        myProfile=view.findViewById(R.id.myProfile)
        auth= FirebaseAuth.getInstance()
       var exitProfile:TextView=view.findViewById(R.id.exitProfile)


        closeApp.setOnClickListener {
            requireActivity().finish()
        }
        destekleElaqe.setOnClickListener {
            Toast.makeText(requireContext(),"Bu funksiya yaxın zamanda aktiv ediləcək",Toast.LENGTH_SHORT).show()
        }
        profilPaylas.setOnClickListener {

            Toast.makeText(requireContext(),"Bu funksiya yaxın zamanda aktiv ediləcək",Toast.LENGTH_SHORT).show()
        }

        exitProfile.setOnClickListener {
            val alertDialog= AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Diqqət!")
                .setMessage("Hesabdan çıxmağa əminsiniz?")
                .setCancelable(false)
                .setPositiveButton("Bəli"){
                        dialog,which->
                    auth.signOut()
                    auth.signInAnonymously()
                    Toast.makeText(requireContext(),"Hesabdan çıxıldı.",Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireActivity(),LogRegActivity::class.java))
                    requireActivity().finish()
                }.setNegativeButton("Xeyr"){dialog,which->
                    return@setNegativeButton
                }.show()

        }
    changePass.setOnClickListener {
        val navDirections=SettingsFragmentDirections.actionSettingsFragmentToProfilePassChangeFragment()
        Navigation.findNavController(requireActivity(),R.id.fragmentContainerView).navigate(navDirections)

    }

        lin1.visibility=View.GONE
        lin2.visibility=View.GONE
        lin3.visibility=View.GONE

        hesabAyar.setOnClickListener {
            if(lin1.isVisible){
                lin1.visibility=View.GONE
            }else{
                lin1.visibility=View.VISIBLE
            }

        }

        proqAyar.setOnClickListener {
            if(lin2.isVisible){
                lin2.visibility=View.GONE
            }else{
                lin2.visibility=View.VISIBLE
            }

        }

        exit.setOnClickListener {
            if(lin3.isVisible){
                lin3.visibility=View.GONE
            }else{
                lin3.visibility=View.VISIBLE
            }

        }

        myProfile.setOnClickListener {
            val navDirections=SettingsFragmentDirections.actionSettingsFragmentToMyProfileFragment()
            Navigation.findNavController(requireActivity(),R.id.fragmentContainerView).navigate(navDirections)
        }


        goBack.setOnClickListener {
            val navDirections=SettingsFragmentDirections.actionSettingsFragmentToProfileFragment()
            Navigation.findNavController(requireActivity(),R.id.fragmentContainerView).navigate(navDirections)
        }

    }



}