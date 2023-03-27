package com.bashirli.mysocial.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.bashirli.mysocial.R
import com.bashirli.mysocial.fragment.logreg.LoginFragmentDirections
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import java.util.UUID

class AddPostFragment : Fragment() {
    private lateinit var hideImage:TextView
    private lateinit var nickname:TextView
    private lateinit var postText:EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage:FirebaseStorage
    private lateinit var storageReference: StorageReference
    private var selectedImage:Uri?=null
    private lateinit var goBack:TextView
    private lateinit var imageSelect:ImageView
    private lateinit var imageSelectButton:TextView
    private lateinit var button:Button
    private lateinit var imageView7:ImageView
    private lateinit var activityResultLauncher:ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var userNickname:String



   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_post, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authEverything(view)
        userInfo()
        activityLaunch()


    }

    private fun authEverything(view: View){
        auth= FirebaseAuth.getInstance()
        firestore= FirebaseFirestore.getInstance()
        storage= FirebaseStorage.getInstance()
        storageReference=storage.getReference()
        nickname=view.findViewById(R.id.nameOfPublisher)
        postText=view.findViewById(R.id.editTextTextMultiLine)
        imageSelect=view.findViewById(R.id.selectingImage)
        button=view.findViewById(R.id.button3)
        imageSelectButton=view.findViewById(R.id.imageSelectButton)
        goBack=view.findViewById(R.id.goBackAddPost)
        imageSelect.visibility=View.GONE
        hideImage=view.findViewById(R.id.imageHide)
        hideImage.visibility=View.INVISIBLE
        imageView7=view.findViewById(R.id.imageView7)
        button.setOnClickListener{
            publishPost(it)
        }
        imageSelect.setOnClickListener{
            selectMyImage(it)
        }


        goBack.setOnClickListener {
            val alertDialog= AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Diqqət!").setMessage("Geri dönməyə əminsiniz?")
                .setNegativeButton("Xeyr"){
                        dialog,which->return@setNegativeButton
                }.setPositiveButton("Bəli"){
                        dialog,which->
                    val navDirections = AddPostFragmentDirections.actionAddPostFragmentToFeedFragment()
                    Navigation.findNavController(requireActivity(),R.id.fragmentContainerView)
                        .navigate(navDirections)
                }.show()
        }

        imageSelectButton.setOnClickListener {
            imageSelect.visibility=View.VISIBLE
            imageSelectButton.visibility=View.INVISIBLE
            hideImage.visibility=View.VISIBLE
        }
        hideImage.setOnClickListener {
            if(selectedImage!=null){
                val alertDialog=AlertDialog.Builder(requireContext())
                alertDialog.setTitle("Diqqət!").setCancelable(false).setMessage("Əgər şəkili bağlasanız, seçdiyiniz şəkil payla" +
                        "şılmayacaq. Yenə də bağlamağa əminsiniz?")
                    .setNegativeButton("Xeyr"){dialog,which->
                        return@setNegativeButton
                    }.setPositiveButton("Bəli"){dialog,which->
                        imageSelect.visibility=View.GONE
                        imageSelectButton.visibility=View.VISIBLE
                        hideImage.visibility=View.INVISIBLE
                        imageSelect.setImageResource(R.drawable.sekilsec)
                        selectedImage=null
                    }.show()
            }else{
                imageSelect.visibility=View.GONE
                imageSelectButton.visibility=View.VISIBLE
                hideImage.visibility=View.INVISIBLE

            }

        }

    }


    private fun selectMyImage(view: View){
        if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
        if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
            Snackbar.make(view,"Şəkil seçməyə icazə yoxdur.",Snackbar.LENGTH_INDEFINITE).setAction("İcazə ver"){
                //permission
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }.show()
        }else {
            //permission

            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        }else {
            //activity
            activityResultLauncher.launch(Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI))

        }


    }

    private fun activityLaunch(){
        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode==Activity.RESULT_OK){
                var intent=it.data
                if(intent!=null){
                    selectedImage=intent.data
                    try {
                        imageSelect.setImageURI(selectedImage)

                    }catch (e:Exception){
                        Toast.makeText(requireContext(),e.localizedMessage,Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }

        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){
           if(it!!){
               activityResultLauncher.launch(Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
           }else{
               Toast.makeText(requireContext(),"İcazə verilmədi",Toast.LENGTH_SHORT).show()
           }
        }


    }



    private fun publishPost(view:View){
    if(postText.text.toString().equals("")){
        return
    }

        val alertDialog =AlertDialog.Builder(requireContext())
        alertDialog.setTitle("Diqqət!").setMessage("Paylaşmağa əminsiniz?")
            .setNegativeButton("Xeyr"){dialog,which->
                return@setNegativeButton
            }.setPositiveButton("Bəli"){dialog,which->

                val uuid=UUID.randomUUID()
                val imageData="images/"+uuid+".jpg"
                val progresDialog=ProgressDialog(requireContext())
                progresDialog.setTitle("Paylaşılır")
                progresDialog.setMessage("Zəhmət olmasa gözləyin")
                progresDialog.setCancelable(false)
                progresDialog.show()

                if(selectedImage!=null) {
                    storageReference.child(imageData).putFile(selectedImage!!).addOnSuccessListener {
                        storage.getReference(imageData).downloadUrl.addOnSuccessListener {
                            val postData=HashMap<String,Any>()
                            postData.put("date",FieldValue.serverTimestamp())
                            postData.put("text",postText.text.toString())
                            postData.put("email",auth.currentUser!!.email!!)
                            postData.put("nickname",userNickname)
                            postData.put("image",it.toString())
                            postData.put("view","0")
                            firestore.collection("Post").add(postData).addOnSuccessListener {
                                Toast.makeText(requireContext(),"Post paylaşıldı",Toast.LENGTH_SHORT).show()
                                progresDialog.cancel()
                                var activityData=HashMap<String,Any>()
                                activityData.put("email",auth.currentUser!!.email!!)
                                activityData.put("date",FieldValue.serverTimestamp())
                                activityData.put("info"," tarixində post paylaşıldı.")
                                firestore.collection("Activity").document("activityData")
                                    .collection(auth.currentUser!!.email!!).add(activityData)
                                val NavDirections=AddPostFragmentDirections.actionAddPostFragmentToFeedFragment()
                                Navigation.findNavController(requireActivity(),R.id.fragmentContainerView)
                                    .navigate(NavDirections)



                            }.addOnFailureListener {
                                Toast.makeText(requireContext(),it.localizedMessage,Toast.LENGTH_SHORT).show()
                                progresDialog.cancel()
                            }

                        }.addOnFailureListener {
                            Toast.makeText(requireContext(),it.localizedMessage,Toast.LENGTH_SHORT).show()
                            progresDialog.cancel()
                        }

                    }.addOnFailureListener{
                        Toast.makeText(requireContext(),it.localizedMessage,Toast.LENGTH_SHORT).show()
                        progresDialog.cancel()
                    }

                }else {
                    val postData=HashMap<String,Any>()
                    postData.put("date",FieldValue.serverTimestamp())
                    postData.put("text",postText.text.toString())
                    postData.put("email",auth.currentUser!!.email!!)
                    postData.put("nickname",userNickname)
                    postData.put("image","null")
                    postData.put("view","0")
                    firestore.collection("Post").add(postData).addOnSuccessListener {
                       var handler=Handler()
                        handler.postDelayed({
                            Toast.makeText(requireContext(),"Post paylaşıldı",Toast.LENGTH_SHORT).show()
                            progresDialog.cancel()
                            var activityData=HashMap<String,Any>()
                            activityData.put("email",auth.currentUser!!.email!!)
                            activityData.put("date",FieldValue.serverTimestamp())
                            activityData.put("info"," tarixində post paylaşıldı.")

                            firestore.collection("Activity").document("activityData")
                                .collection(auth.currentUser!!.email!!).add(activityData)
                            val NavDirections=AddPostFragmentDirections.actionAddPostFragmentToFeedFragment()
                            Navigation.findNavController(requireActivity(),R.id.fragmentContainerView).navigate(NavDirections)
                        },1300)


                    }.addOnFailureListener{
                        Toast.makeText(requireContext(),it.localizedMessage,Toast.LENGTH_SHORT).show()
                        progresDialog.cancel()

                    }

                }

            }.show()


    }


    private fun userInfo(){
        firestore.collection("UserData").document(auth.currentUser!!.email!!).addSnapshotListener { value, error ->
        if(error!=null){
            Toast.makeText(requireContext(),error.localizedMessage,Toast.LENGTH_SHORT).show()

        }
            if(value!=null){
                userNickname=value.get("myNick") as String
                nickname.setText(userNickname)
            var image=value.get("profilePicture") as String
                if(image.equals("null")){
                    imageView7.setImageResource(R.drawable.circlelogo)
                }else{
                    Picasso.get().load(image).into(imageView7)
                }

            }



        }



    }



}