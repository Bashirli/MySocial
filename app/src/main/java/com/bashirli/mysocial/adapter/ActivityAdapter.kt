package com.bashirli.mysocial.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.layout.Layout
import androidx.recyclerview.widget.RecyclerView
import com.bashirli.mysocial.R
import com.bashirli.mysocial.databinding.RecycleractivityBinding
import com.bashirli.mysocial.databinding.RecyclercommentBinding
import com.bashirli.mysocial.model.ActivityModel
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ActivityAdapter(var arrayList: ArrayList<ActivityModel>) : RecyclerView.Adapter<ActivityAdapter.ActivityHolder>() {
    class ActivityHolder(var binding:RecycleractivityBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityHolder {
    var recycleractivityBinding=RecycleractivityBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ActivityHolder(recycleractivityBinding)
    }

    override fun onBindViewHolder(holder: ActivityHolder, position: Int) {
    var firestore:FirebaseFirestore= FirebaseFirestore.getInstance()
        firestore.collection("UserData").document(arrayList.get(position).email)
            .addSnapshotListener { value, error ->
                if(error!=null){
                    Toast.makeText(holder.itemView.context,error.localizedMessage,Toast.LENGTH_SHORT).show()
                }
               if(value!=null){
                   holder.binding.recyclerName.setText(value.get("myNick") as String)
                   var ppInfo=value.get("profilePicture") as String

                   if(ppInfo.equals("null")){
                       holder.binding.recyclerActivityImage.setImageResource(R.drawable.logo)

                   }else{
                       Picasso.get().load(value.get("profilePicture") as String).into(holder.binding.recyclerActivityImage)

                   }

               }else {
                   holder.binding.recyclerName.setText("null")
               holder.binding.recyclerActivityImage.setImageResource(R.drawable.logo)
               }


            }

        var stringInfo=arrayList.get(position).date+" "+arrayList.get(position).info
        holder.binding.recyclerInfo.setText(stringInfo)



    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}