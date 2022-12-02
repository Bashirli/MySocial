package com.bashirli.mysocial.adapter

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bashirli.mysocial.R
import com.bashirli.mysocial.activity.PostCheckActivity
import com.bashirli.mysocial.activity.ScreenActivity
import com.bashirli.mysocial.databinding.RecyclercommentBinding
import com.bashirli.mysocial.model.ComModel
import com.bashirli.mysocial.model.Model
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ComAdapter(var arrayList: ArrayList<ComModel>,var publisherEmail:String,var postId:String,var myData:Model) : RecyclerView.Adapter<ComAdapter.AdapterHolder>() {
    class AdapterHolder(var binding:RecyclercommentBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterHolder {
    var RecyclercommentBinding=RecyclercommentBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AdapterHolder(RecyclercommentBinding)
    }

    override fun onBindViewHolder(holder: AdapterHolder, position: Int) {
        holder.binding.deleteCom.visibility= View.GONE
     if(arrayList.get(position).userPP.equals("null")){
         holder.binding.comImage.setImageResource(R.drawable.logo)

     }else{
         Picasso.get().load(arrayList.get(position).userPP).into(holder.binding.comImage)
     }
        holder.binding.comName.setText(arrayList.get(position).userNick)
        holder.binding.textView20.setText(arrayList.get(position).comment)
        var auth:FirebaseAuth= FirebaseAuth.getInstance()


        if(auth.currentUser!!.email!!.equals(arrayList.get(position).email)
            ||publisherEmail.equals(auth.currentUser!!.email!!)){
            holder.binding.deleteCom.visibility= View.VISIBLE
            holder.binding.deleteCom.setOnClickListener {
                deleteComment(it,holder,arrayList.get(position),position)
            }
        }

    }

    private fun deleteComment(view:View,holder: AdapterHolder,comModel:ComModel,position: Int){
        var firestore:FirebaseFirestore= FirebaseFirestore.getInstance()
        val alertDialog= AlertDialog.Builder(holder.itemView.context)
        alertDialog.setTitle("Diqqət").setMessage("Bu yorumu silmək istəyirsiniz?").setCancelable(false)
            .setNegativeButton("Xeyr"){
                    dialog,which->
                return@setNegativeButton

            }.setPositiveButton("Bəli"){
                    dialog,which->
                val progressDialog= ProgressDialog(holder.itemView.context)
                progressDialog.setTitle("Silinir")
                progressDialog.setMessage("Zəhmət olmasa gözləyin")
                progressDialog.setCancelable(false)
                progressDialog.show()
        firestore.collection("Comment").document("commentData").collection(postId)
            .document(comModel.id).delete().addOnFailureListener {
                Toast.makeText(holder.itemView.context,it.localizedMessage,Toast.LENGTH_SHORT).show()
                progressDialog.cancel()
            }.addOnSuccessListener {
                Toast.makeText(holder.itemView.context,"Yorum silindi",Toast.LENGTH_SHORT).show()
                progressDialog.cancel()
                var intent=Intent(holder.itemView.context,ScreenActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                holder.itemView.context.startActivity(intent)

            }


            }.show()
    }


    override fun getItemCount(): Int {
        return arrayList.size
    }
}