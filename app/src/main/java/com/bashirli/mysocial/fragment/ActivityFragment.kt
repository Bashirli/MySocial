package com.bashirli.mysocial.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bashirli.mysocial.R
import com.bashirli.mysocial.adapter.ActivityAdapter
import com.bashirli.mysocial.model.ActivityModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ActivityFragment : Fragment() {
    private lateinit var goBack:TextView
    private lateinit var recycler:RecyclerView
    private lateinit var arrayList: ArrayList<ActivityModel>
    private lateinit var auth:FirebaseAuth
    private lateinit var adapter:ActivityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        goBack=view.findViewById(R.id.goBackActivity)
        recycler=view.findViewById(R.id.recyclerActivity)

        goBack.setOnClickListener {
            val navDirections=ActivityFragmentDirections.actionActivityFragmentToFeedFragment()
            Navigation.findNavController(requireActivity(),R.id.fragmentContainerView).navigate(navDirections)
        }
        auth= FirebaseAuth.getInstance()
        arrayList= ArrayList()
        recycler.layoutManager=LinearLayoutManager(requireContext())
        adapter= ActivityAdapter(arrayList)
        recycler.adapter=adapter

        getData()


    }

private fun getData(){
    var progressDialog=ProgressDialog(requireContext())
    progressDialog.setCancelable(false)
    progressDialog.setTitle("Yüklənir")
    progressDialog.setMessage("Zəhmət olmasa gözləyin")
    progressDialog.show()

    arrayList.clear()
    var firestore:FirebaseFirestore= FirebaseFirestore.getInstance()
    var handler=Handler()
    handler.postDelayed({
        firestore.collection("Activity").document("activityData").collection(auth.currentUser!!.email!!)
            .orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { value, error ->
                if(error!=null){
                    Toast.makeText(requireContext(),error.localizedMessage,Toast.LENGTH_SHORT).show()
                }
                value?.documents.let {
                    it?.let {
                        it.forEach {
                            var timestamp=it.get("date") as Timestamp
                            var date=timestamp.toDate()
                            var dateFormat=android.text.format.DateFormat.getDateFormat(requireContext())
                            var myDate=dateFormat.format(date).toString()

                            var model=ActivityModel(it.get("info") as String,myDate,auth.currentUser!!.email!!)
                            arrayList.add(model)
                        }
                    }


                }
                adapter.notifyDataSetChanged()
                progressDialog.cancel()

            }
    },1000)

}



}