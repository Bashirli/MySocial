package com.bashirli.mysocial.fragment

import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bashirli.mysocial.R
import com.bashirli.mysocial.adapter.DataAdapter
import com.bashirli.mysocial.model.Model
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.DateFormat

class FeedFragment : Fragment() {
    private lateinit var recyclerFeed:RecyclerView
    private lateinit var messageButton:TextView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var arrayList: ArrayList<Model>
    private lateinit var adapter:DataAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var cm=requireActivity().getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(cm.activeNetworkInfo==null){
            val alert= AlertDialog.Builder(requireContext())
            alert.setTitle("Diqqət!").setMessage("İnternetiniz düz işləmir! Proqram internetsiz işləyə bilməz!").setCancelable(false)
            alert.setNegativeButton("Ok"){
                    dialog,which->requireActivity().finish()
            }.show()
        }

        auth= FirebaseAuth.getInstance()
        firestore= FirebaseFirestore.getInstance()




        recyclerFeed=view.findViewById(R.id.recyclerFeed)
        messageButton=view.findViewById(R.id.textView3)
          messageButton.setOnClickListener{
         Toast.makeText(requireContext(),"Bu funksiya tezliklə aktiv olacaq.",Toast.LENGTH_SHORT).show()
        }

        arrayList=ArrayList<Model>()

    recyclerFeed.layoutManager=LinearLayoutManager(requireContext())
        adapter= DataAdapter(arrayList)
        recyclerFeed.adapter=adapter


    getData()

    }


    private fun getData(){

    firestore.collection("Post").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { value, error ->
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

    override fun onDestroyView() {
        super.onDestroyView()

    }

}