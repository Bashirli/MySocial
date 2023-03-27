package com.bashirli.mysocial.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bashirli.mysocial.R
import com.bashirli.mysocial.adapter.DataAdapter
import com.bashirli.mysocial.model.Model
import com.google.api.Distribution.BucketOptions.Linear
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SearchFragment : Fragment() {
        private lateinit var auth: FirebaseAuth
        private lateinit var firestore: FirebaseFirestore
        private lateinit var goBack:TextView
        private lateinit var searchBox:EditText
        private lateinit var button: Button
        private lateinit var recycler:RecyclerView
        private lateinit var infoText:TextView
        private lateinit var arrayList: ArrayList<Model>
        private lateinit var adapter: DataAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth= FirebaseAuth.getInstance()

        goBack=view.findViewById(R.id.goBackSearch)
        recycler=view.findViewById(R.id.recyclerSearch)
        searchBox=view.findViewById(R.id.editTextTextPersonName7)
        button=view.findViewById(R.id.button6)
        infoText=view.findViewById(R.id.textView22)
        arrayList=ArrayList()



        goBack.setOnClickListener {
            val navDirections=SearchFragmentDirections.actionSearchFragmentToFeedFragment()
            Navigation.findNavController(requireActivity(),R.id.fragmentContainerView).navigate(navDirections)
        }

        recycler.layoutManager=LinearLayoutManager(requireContext())
        adapter= DataAdapter(arrayList)
        recycler.adapter=adapter

        button.setOnClickListener {
            search(it)
        }

    }


    private fun search(view:View){
    if(searchBox.text.toString().equals("")){
        Toast.makeText(requireContext(),"Xana boş buraxılıb",Toast.LENGTH_SHORT).show()
        return
    }
        val progressDialog=ProgressDialog(requireContext())
        progressDialog.setTitle("Axtarılır")
        progressDialog.setMessage("Zəhmət olmasa gözləyin")
        progressDialog.setCancelable(false)
        progressDialog.show()
        firestore= FirebaseFirestore.getInstance()
    arrayList.clear()
    firestore.collection("Post").addSnapshotListener { value, error ->
        if(error!=null){
            Toast.makeText(requireContext(),error.localizedMessage,Toast.LENGTH_SHORT).show()
            return@addSnapshotListener
        }
        if(value!=null){

            var searchedElement=searchBox.text.toString().lowercase()


            value.documents.forEach {
                var dataText=(it.get("text") as String).toString().lowercase()
                var dataName=(it.get("nickname") as String).toString().lowercase()

                if(dataName.contains(searchedElement)||dataName.equals(searchedElement)||
                        dataText.contains(searchedElement)||dataText.equals(searchedElement)){
                    var email=it.get("email") as String
                    var image=it.get("image") as String
                    var nickname=it.get("nickname") as String
                    var text=it.get("text") as String
                    var date=it.get("date").toString()
                    var viewCount=it.get("view") as String
                    var id=it.id

                    firestore.collection("UserData").document(email).addSnapshotListener { value, error ->
                        var userPP=value!!.get("profilePicture") as String
                        var newModel=Model(email,nickname,text, viewCount, userPP,image,date,id)
                        arrayList.add(newModel)
                        infoText.setText("Axtarış edin.")
                        infoText.visibility=View.GONE
                        adapter.notifyDataSetChanged()

                    }


                }

                if(arrayList.size==0){
                infoText.setText("Belə bir şey tapılmadı")
                infoText.visibility=View.VISIBLE

            }

                arrayList.clear()


            }




        }

    }
        var handler=Handler()
        handler.postDelayed({
            progressDialog.cancel()
        },500)

    }


}