package com.bashirli.mysocial.fragment.profile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.InputType
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.bashirli.mysocial.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class MyProfileFragment : Fragment() {
    private lateinit var imageView: ImageView
    private lateinit var button: Button
    private lateinit var goBack:TextView
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var selectedImage: Uri?=null
    private lateinit var email:EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var nickname:EditText
    private lateinit var bio:EditText
    private lateinit var number:EditText
    private lateinit var  progressDialog:ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_profile, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nickname=view.findViewById(R.id.editTextTextPersonName4)
        number=view.findViewById(R.id.editTextPhone)
        bio=view.findViewById(R.id.editTextTextMultiLine2)
        email=view.findViewById(R.id.editTextTextPersonName5)
        imageView=view.findViewById(R.id.imageView6)
       button=view.findViewById(R.id.button5)
        auth= FirebaseAuth.getInstance()
        firestore= FirebaseFirestore.getInstance()
        goBack=view.findViewById(R.id.textView7)
        storage= FirebaseStorage.getInstance()
        storageReference=storage.getReference()

        email.inputType=InputType.TYPE_NULL
        email.setText(auth.currentUser!!.email!!)

       updateData()
        activityLaunch()


        imageView.setOnClickListener{
            val alertDialog= AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Profil şəkli").setMessage("Nə etmək istəyirsiniz?")
                .setNegativeButton("Profil şəklini sil"){
                        dialog,which->imageView.setImageResource(R.drawable.logo)
                    storageReference.child("pp/"+auth.currentUser?.email+".jpg").delete()
                    firestore.collection("UserData").document(auth.currentUser?.email!!)
                        .update("profilePicture","null").addOnSuccessListener {

                Toast.makeText(requireContext(),"Profil şəkli silindi!",Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                Toast.makeText(requireContext(),it.localizedMessage,Toast.LENGTH_SHORT).show()
                    }

                    selectedImage=null

                }.setPositiveButton("Profil şəkli seç"){
                        dialog,which->
                    selectMyImage(it)
                }.show()




        }

        button.setOnClickListener{
            but(it)
        }


        goBack.setOnClickListener {
            _goBack()
        }

    }

    private fun errorFind():Int{
    if(nickname.text.toString().equals("")){
        Toast.makeText(requireContext(),"Xana boş buraxılıb",Toast.LENGTH_SHORT).show()
        return 0
    }
        if(nickname.text.toString().length>25){
            Toast.makeText(requireContext(),"Nickname çox uzundur! (Maks 25 Simvol)",Toast.LENGTH_SHORT).show()
            return 0
        }


        return 1
    }


    private fun but(view: View){
    if(errorFind()==0){
        return
    }


         progressDialog=ProgressDialog(requireContext())
        progressDialog.setTitle("Yenilənir")
        progressDialog.setMessage("Zəhmət olmasa gözləyin")
        progressDialog.setCancelable(false)
        progressDialog.show()
        var imageData="pp/"+auth.currentUser?.email+".jpg"
        if(selectedImage!=null) {
            storageReference.child(imageData).putFile(selectedImage!!).addOnSuccessListener {
                storage.getReference(imageData).downloadUrl.addOnSuccessListener {
                    val data=HashMap<String,Any>()
                    data.put("myNick",nickname.text.toString())
                    if(bio.text.toString().equals("")){
                        data.put("bio","null")
                    }else{
                        data.put("bio",bio.text.toString())
                    }


                    if(number.text.toString().equals("")){
                        data.put("number","null")
                    }else{
                        data.put("number",number.text.toString())
                    }
                    data.put("profilePicture",it.toString())
                    data.put("email",auth.currentUser!!.email!!)

                    firestore.collection("UserData").document(auth.currentUser!!.email!!)
                        .update(data).addOnSuccessListener {
                            updatePostNames()
                            Toast.makeText(requireContext(), "Məlumatlar uğurla yeniləndi!", Toast.LENGTH_SHORT)
                                .show()
                            progressDialog.cancel()
                        }.addOnFailureListener {
                            Toast.makeText(requireContext(),it.localizedMessage,Toast.LENGTH_SHORT).show()

                            progressDialog.cancel()
                        }
                }
            }.addOnFailureListener{
                Toast.makeText(requireContext(),it.localizedMessage,Toast.LENGTH_SHORT).show()

                progressDialog.cancel()
            }

        }
        else{
            val data=HashMap<String,Any>()
            data.put("myNick",nickname.text.toString())
            if(bio.text.toString().equals("")){
                data.put("bio","null")
            }else{
                data.put("bio",bio.text.toString())
            }


            if(number.text.toString().equals("")){
                data.put("number","null")
            }else {
                data.put("number", number.text.toString())
            }

            firestore.collection("UserData").document(auth.currentUser!!.email!!)
                .addSnapshotListener{value,error->
                    if(error!=null){
                        Toast.makeText(requireContext(),error.localizedMessage.toString(),Toast.LENGTH_SHORT).show()
                    }
                    val testImage=value!!.get("profilePicture") as String
                    if(testImage.equals("null")){
                        data.put("profilePicture","null")
                    }else {
                        data.put("profilePicture",testImage)
                    }
                }

            data.put("email",auth.currentUser!!.email!!)

            firestore.collection("UserData").document(auth.currentUser!!.email!!)
                .update(data).addOnSuccessListener {
                    updatePostNames()
                    Toast.makeText(requireContext(), "Məlumatlar uğurla yeniləndi!", Toast.LENGTH_SHORT)
                        .show()
                    progressDialog.cancel()
                }.addOnFailureListener {
                    Toast.makeText(requireContext(),it.localizedMessage,Toast.LENGTH_SHORT).show()

                    progressDialog.cancel()
                }

        }



    }


    private fun selectMyImage(view: View){
        if(ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Şəkil seçməyə icazə yoxdur.", Snackbar.LENGTH_INDEFINITE).setAction("İcazə ver"){
                    //permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show()
            }else {
                //permission

                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }else {
            //activity
            activityResultLauncher.launch(
                Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            )

        }


    }



    private fun activityLaunch(){
        activityResultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode== Activity.RESULT_OK){
                var intent=it.data
                if(intent!=null){
                    selectedImage=intent.data
                    try {
                        imageView.setImageURI(selectedImage)

                    }catch (e:Exception){
                        Toast.makeText(requireContext(),e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }

        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it!!){
                activityResultLauncher.launch(
                    Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                )
            }else{
                Toast.makeText(requireContext(),"İcazə verilmədi", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun _goBack(){
        val alertDialog= AlertDialog.Builder(requireContext())
        alertDialog.setTitle("Diqqət!").setMessage("Geri dönməyə əminsiniz?")
            .setNegativeButton("Xeyr"){ dialog,which->
            return@setNegativeButton
            }.setPositiveButton("Bəli"){
                    dialog,which->
                val NavDirections=MyProfileFragmentDirections.actionMyProfileFragmentToSettingsFragment()
                Navigation.findNavController(requireActivity(),R.id.fragmentContainerView).navigate(NavDirections)
            }.show()


    }

    private fun updateData(){
        val progressDialog=ProgressDialog(requireContext())
        progressDialog.setTitle("Yüklənir")
        progressDialog.setMessage("Zəhmət olmasa gözləyin")
        progressDialog.setCancelable(false)
        progressDialog.show()


        firestore.collection("UserData").document(auth.currentUser!!.email!!)
            .addSnapshotListener{value,error->
            if(error!=null){
                Toast.makeText(requireContext(),error.localizedMessage,Toast.LENGTH_SHORT).show()

            }
            if(value!=null){
                val nick=value.get("myNick") as String
                val myBio=value.get("bio") as String
                val pp=value.get("profilePicture") as String
                val num=value.get("number") as String

                nickname.setText(nick)

                if(myBio.equals("null")){
                    bio.setText("")
                }else{
                    bio.setText(myBio)
                }

                if(pp.equals("null")){
                    imageView.setImageResource(R.drawable.logo)
                }else{
                    Picasso.get().load(pp).into(imageView)
                }
                if(num.equals("null")){
                    number.setText("")
                }else{
                    number.setText(num)
                }


            }

        progressDialog.cancel()
        }



    }

    private fun updatePostNames(){
        lateinit var savedImage:String

        firestore.collection("UserData").document(auth.currentUser!!.email!!).addSnapshotListener { value, error ->
       savedImage= value!!.get("profilePicture") as String

        }
        firestore.collection("Post").whereEqualTo("email",auth.currentUser!!.email!!).addSnapshotListener { value, error ->
            if(error!=null){
                Toast.makeText(requireContext(),error.localizedMessage,Toast.LENGTH_SHORT).show()
            }
            if(value!=null) {

                value.documents.forEach {

                        var postId=it.id
                      firestore.collection("Post").document(postId)
                          .update("nickname",nickname.text.toString())
            }
      }
        }



            firestore.collection("Post").addSnapshotListener { value, error ->
                if(error!=null){
                    Toast.makeText(requireContext(),error.localizedMessage,Toast.LENGTH_SHORT).show()
                }
                if(value!=null) {
                    value.documents.forEach {
                        firestore.collection("Comment").document("commentData")
                            .collection(it.id).whereEqualTo("email",auth.currentUser!!.email!!).addSnapshotListener { value, error ->
                                var postId=it.id

                            if(error!=null){
                                Toast.makeText(requireContext(),error.localizedMessage,Toast.LENGTH_SHORT).show()
                            }
                            if(value!=null) {
                                value.documents.forEach {
                                    firestore.collection("Comment").document("commentData")
                                        .collection(postId).document(it.id)
                                        .update("userNick",nickname.text.toString(),"userPP",savedImage)
                                }
                            }

                    }

                }


                }
            }


                }








    override fun onDestroyView() {
        super.onDestroyView()
        var handler=Handler()
        handler.postDelayed({
            firestore.terminate()
        },1500)


    }

}