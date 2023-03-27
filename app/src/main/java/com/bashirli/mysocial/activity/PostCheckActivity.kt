package com.bashirli.mysocial.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bashirli.mysocial.R
import com.bashirli.mysocial.adapter.ComAdapter
import com.bashirli.mysocial.databinding.ActivityPostCheckBinding
import com.bashirli.mysocial.model.ComModel
import com.bashirli.mysocial.model.Model
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class PostCheckActivity : AppCompatActivity() {
    private lateinit var binding:ActivityPostCheckBinding
    private lateinit var myData:Model
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var userPP:String
    private lateinit var userNick:String
    private lateinit var adapter:ComAdapter
    private lateinit var arrayList:ArrayList<ComModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityPostCheckBinding.inflate(layoutInflater)
        var view=binding.root
        setContentView(view)
        firestore= FirebaseFirestore.getInstance()
        auth= FirebaseAuth.getInstance()
        myData=intent.getSerializableExtra("data") as Model
        updateData()
        binding.deletePost.visibility=View.GONE

        if(myData.email.equals(auth.currentUser!!.email!!)){
            binding.deletePost.visibility=View.VISIBLE
        }
        binding.deletePost.setOnClickListener {
            deletePost(it)
        }
        binding.gobackPostCheck.setOnClickListener {
            finish()
        }
        binding.button4.setOnClickListener {
        if(auth.currentUser!!.isAnonymous){
            Toast.makeText(this@PostCheckActivity,"Hesaba giriş edin ki, yorum paylaşa biləsiniz.",Toast.LENGTH_SHORT).show()
        }else{

            publishComment(it)

        }
        }

         arrayList=ArrayList<ComModel>()
        binding.recyclerCom.layoutManager=LinearLayoutManager(this@PostCheckActivity)
        adapter= ComAdapter(arrayList,myData.email,myData.id,myData)
        binding.recyclerCom.adapter=adapter

        getData()



    }

    private fun getData(){

        firestore.collection("Comment").document("commentData").collection(myData.id)
            .addSnapshotListener{value,error->
                if(error!=null){
                    Toast.makeText(this@PostCheckActivity,error.localizedMessage,Toast.LENGTH_SHORT).show()
                }
                if(value!=null){
                    for (snapshot in value.documents){
                        var email=snapshot.get("email") as String
                        var comment=snapshot.get("comment") as String
                        var comNick=snapshot.get("userNick") as String
                        var comPP=snapshot.get("userPP") as String
                        var commodel=ComModel(comment,email,comNick,comPP,snapshot.id)
                        arrayList.add(commodel)

                    }
                    adapter.notifyDataSetChanged()

                }

        }
    }


    private fun deletePost(view:View){
        val alertDialog=AlertDialog.Builder(this@PostCheckActivity)
        alertDialog.setTitle("Diqqət").setMessage("Bu postu silmək istəyirsiniz?").setCancelable(false)
            .setNegativeButton("Xeyr"){
                    dialog,which->
                return@setNegativeButton

            }.setPositiveButton("Bəli"){
                dialog,which->
                val progressDialog=ProgressDialog(this@PostCheckActivity)
                progressDialog.setTitle("Silinir")
                progressDialog.setMessage("Zəhmət olmasa gözləyin")
                progressDialog.setCancelable(false)
                progressDialog.show()
            firestore.collection("Post").document(myData.id).delete().addOnSuccessListener {
                Toast.makeText(this@PostCheckActivity,"Uğurla silindi.",Toast.LENGTH_SHORT).show()

                var activityData=HashMap<String,Any>()
                activityData.put("email",auth.currentUser!!.email!!)
                activityData.put("date", FieldValue.serverTimestamp())
                activityData.put("info"," tarixində post silindi.")

                firestore.collection("Activity").document("activityData")
                    .collection(auth.currentUser!!.email!!).add(activityData)
                finish()
                progressDialog.cancel()
            }.addOnFailureListener {
                Toast.makeText(this@PostCheckActivity,it.localizedMessage,Toast.LENGTH_SHORT).show()
                progressDialog.cancel()

            }


            }.show()
    }


    private fun publishComment(view:View){
    if(binding.editTextTextPersonName6.text.toString().equals("")){
        Toast.makeText(this@PostCheckActivity,"Boş yorum paylaşıla bilməz.",Toast.LENGTH_SHORT).show()
        return
    }
        arrayList.clear()
        val progressDialog=ProgressDialog(this@PostCheckActivity)
        progressDialog.setTitle("Paylaşılır")
        progressDialog.setMessage("Zəhmət olmasa gözləyin")
        progressDialog.setCancelable(false)
        progressDialog.show()
        firestore.collection("UserData").document(auth.currentUser!!.email!!).addSnapshotListener { value, error ->
            if(error!=null){
                Toast.makeText(this@PostCheckActivity,error.localizedMessage,Toast.LENGTH_SHORT).show()
            }
            if(value!=null) {
                userPP=value.get("profilePicture") as String
                userNick=value.get("myNick") as String
                var comData=HashMap<String,Any>()
                comData.put("comment",binding.editTextTextPersonName6.text.toString())
                comData.put("userNick",userNick)
                comData.put("email",auth.currentUser!!.email!!)
                comData.put("userPP",userPP)
                firestore.collection("Comment").document("commentData")
                    .collection(myData.id).add(comData).addOnSuccessListener {
                        Toast.makeText(this@PostCheckActivity,"Yorum paylaşıldı!",Toast.LENGTH_SHORT).show()

                        progressDialog.cancel()
                    }.addOnFailureListener {
                        Toast.makeText(this@PostCheckActivity,it.localizedMessage,Toast.LENGTH_SHORT).show()
                        progressDialog.cancel()
                    }
            }
        }




    }




    private fun updateData(){
        binding.lookNick.setText(myData.nickname)
        binding.textView13.setText(myData.text)

        if(myData.image.equals("null")){
            binding.imageView8.visibility= View.GONE
        }else{
            binding.imageView8.visibility= View.VISIBLE
            Picasso.get().load(myData.image).into(binding.imageView8)
        }

        if(myData.userPP.equals("null")){
            binding.lookPP.setImageResource(R.drawable.logo)
        }else{
            Picasso.get().load(myData.userPP).into(binding.lookPP)
        }
        firestore.collection("UserData").document(auth.currentUser!!.email!!).addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(this@PostCheckActivity, error.localizedMessage, Toast.LENGTH_SHORT)
                    .show()
            }
            if (value != null) {
                userPP = value.get("profilePicture") as String
                userNick = value.get("myNick") as String
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        firestore.terminate()
    }

}