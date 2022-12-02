package com.bashirli.mysocial.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.bashirli.mysocial.R
import com.bashirli.mysocial.activity.PostCheckActivity
import com.bashirli.mysocial.databinding.RecyclerBinding
import com.bashirli.mysocial.model.Model
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class DataAdapter(var arrayList: ArrayList<Model>) : RecyclerView.Adapter<DataAdapter.DataHolder>() {
    class DataHolder(var binding:RecyclerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataHolder {
      var recyclerBinding=RecyclerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DataHolder(recyclerBinding)
    }

    override fun onBindViewHolder(holder: DataHolder, position: Int) {
        holder.binding.textViewer.setText(arrayList.get(position).viewCount)
        holder.binding.textView16.visibility= View.GONE
        holder.binding.recyclerNickname.setText(arrayList.get(position).nickname)
        if(arrayList.get(position).text.length>130){
            var text=arrayList.get(position).text.removeRange(125,arrayList.get(position).text.length)
            text+="..."
            holder.binding.textView16.visibility= View.VISIBLE
            holder.binding.textView15.setText(text)
        }else {

            holder.binding.textView16.visibility= View.GONE
            holder.binding.textView15.setText(arrayList.get(position).text)
        }

        if(arrayList.get(position).userPP.equals("null")){
            holder.binding.recyclerImage.setImageResource(R.drawable.logo)
        }else{
            Picasso.get().load(arrayList.get(position).userPP).into(holder.binding.recyclerImage)

        }
        if(arrayList.get(position).image.equals("null")){

            holder.binding.recyclerPostImage.visibility=View.GONE
        }else{
            holder.binding.recyclerPostImage.visibility=View.VISIBLE
            Picasso.get().load(arrayList.get(position).image).into(holder.binding.recyclerPostImage)

        }

        holder.binding.textView16.setOnClickListener {
            ardi(it,arrayList.get(position),holder)
        }

        holder.binding.constraintRecycler.setOnClickListener {
            var auth:FirebaseAuth= FirebaseAuth.getInstance()
            if(!auth.currentUser!!.isAnonymous){
                var viewer=arrayList.get(position).viewCount.toInt()
                viewer=viewer+1
                var firestore:FirebaseFirestore= FirebaseFirestore.getInstance()
                firestore.collection("Post").document(arrayList.get(position).id)
                    .update("view",viewer.toString())


                var intent=Intent(holder.itemView.context,PostCheckActivity::class.java)
                intent.putExtra("data",arrayList.get(position))
                holder.itemView.context.startActivity(intent)

            }else{
                Toast.makeText(holder.itemView.context,"Hesaba giriş edin ki, yorumları və postu ətraflı görəsiniz.",Toast.LENGTH_SHORT).show()
            }


        }

    }

    private fun ardi(view:View,data:Model,holder:DataHolder){
        if(holder.binding.textView16.text.equals("Ardı")){
            holder.binding.textView15.setText(data.text)
            holder.binding.textView16.setText("Az göstər")
        }else{
            var text=data.text.removeRange(125,data.text.length)
            text+="..."
            holder.binding.textView16.setText("Ardı")
            holder.binding.textView15.setText(text)
        }

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}