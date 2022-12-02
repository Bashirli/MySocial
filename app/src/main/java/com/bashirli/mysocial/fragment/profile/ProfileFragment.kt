package com.bashirli.mysocial.fragment.profile

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bashirli.mysocial.R
import com.bashirli.mysocial.adapter.DataAdapter
import com.bashirli.mysocial.model.Model
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {
private lateinit var settings:TextView
private lateinit var userName:TextView
private lateinit var profilePicture:ImageView
private lateinit var auth: FirebaseAuth
private lateinit var firestore: FirebaseFirestore
private lateinit var myPosts:RecyclerView
private lateinit var adapter:DataAdapter
private lateinit var arrayList: ArrayList<Model>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth= FirebaseAuth.getInstance()
        firestore= FirebaseFirestore.getInstance()
        settings=view.findViewById(R.id.textView10)
        myPosts=view.findViewById(R.id.myPosts)
        userName=view.findViewById(R.id.userName)
        profilePicture=view.findViewById(R.id.userImage)
        updateData()
        settings.setOnClickListener {
            mySettings(it)
        }


    arrayList=ArrayList<Model>()
        adapter=DataAdapter(arrayList)
        myPosts.layoutManager=LinearLayoutManager(requireContext())
        myPosts.adapter=adapter

        getData()

    }

    private fun getData(){
        firestore.collection("Post").orderBy("date",Query.Direction.DESCENDING)
            .whereEqualTo("email",auth.currentUser!!.email!!)
            .addSnapshotListener { value, error ->
                if(error!=null){
                    Toast.makeText(requireContext(),error.localizedMessage,Toast.LENGTH_SHORT).show()

                }
                if(value!=null){
                    for(snapshot in value.documents){
                        var email=snapshot.get("email") as String
                        var image=snapshot.get("image") as String
                        var nickname=snapshot.get("nickname") as String
                        var text=snapshot.get("text") as String
                        var date=snapshot.get("date").toString()
                        var viewCount=snapshot.get("view") as String
                        var id=snapshot.id

                        firestore.collection("UserData").document(email).addSnapshotListener { value, error ->
                            var userPP=value!!.get("profilePicture") as String
                            var newModel=Model(email,nickname,text, viewCount, userPP,image,date,id)
                            arrayList.add(newModel)
                            adapter.notifyDataSetChanged()

                        }

                        arrayList.clear()

                    }
                }

            }

    }



    private fun updateData(){
        val progressDialog=ProgressDialog(requireContext())
        progressDialog.setTitle("Yüklənir")
        progressDialog.setMessage("Səhifə açılır, zəhmət olmasa gözləyin")
        progressDialog.setCancelable(false)
        progressDialog.show()

        firestore.collection("UserData").document(auth.currentUser!!.email!!).addSnapshotListener { value, error ->
            if(error!=null){
                Toast.makeText(requireContext(),error.localizedMessage,Toast.LENGTH_SHORT).show()
                progressDialog.cancel()
            }
            if(value!=null) {
                userName.setText(value.get("myNick") as String)
                if (value.get("profilePicture").toString().equals("null")) {
                profilePicture.setImageResource(R.drawable.logo)
                    progressDialog.cancel()
                } else {
                    Picasso.get().load(value.get("profilePicture") as String).into(profilePicture)
                    progressDialog.cancel()
                }
            }
        }

    }



    private fun mySettings(view:View){
        val NavDirections=ProfileFragmentDirections.actionProfileFragmentToSettingsFragment()
        Navigation.findNavController(requireActivity(),R.id.fragmentContainerView).navigate(NavDirections)
    }



}