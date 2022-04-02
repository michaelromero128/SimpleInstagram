package com.example.simpleinstagram.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.simpleinstagram.Post
import com.example.simpleinstagram.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.parse.*
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class ComposeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var posts:MutableList<Post>;
    var gotPosts: Boolean = false
    lateinit var ivPreview:ImageView




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivPreview = view.findViewById(R.id.ivPicture)
        view.findViewById<Button>(R.id.btSubmit).setOnClickListener{
            val description = view.findViewById<EditText>(R.id.etDescription).text.toString()
            if(photoFile != null) {
                submitPost(description, ParseUser.getCurrentUser(), photoFile!!)
            }else{
                Log.e("CUSTOMA","Didn't take photo")
            }
        }
        view.findViewById<Button>(R.id.btTakePicture).setOnClickListener{
            onLaunchCamera()

        }
        view.findViewById<Button>(R.id.btLogOut).setOnClickListener{
            ParseUser.logOut();
            activity?.finish();
        }


    }
    fun submitPost(description: String, user: ParseUser, photoFile: File){
        val post = Post()
        post.setDescription(description)
        post.setUser(user)
        post.setImage(ParseFile(photoFile))
        post.saveInBackground{e ->
            if(e != null){
                e.printStackTrace()
            }else{
                Log.i("CUSTOMA","post saved")
                view?.findViewById<EditText>(R.id.etDescription)?.text?.clear()
            }
        }

    }


    val REQUEST_IMAGE_CAPTURE =1
    val photoFileName = "photo.jpg"
    var photoFile: File? = null
    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    private fun onLaunchCamera(){
        Log.i("CUSTOMA", "Camera function launch")
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getPhotoFileUri("photo.jpg")
        if(photoFile != null){
            val fileProvider: Uri = FileProvider.getUriForFile(requireContext(), "com.codepath.fileprovider",photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            if(intent.resolveActivity(requireContext().packageManager) != null){
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }


    fun getPhotoFileUri(fileName:String): File {
        val mediaStorageDir = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),"ParseApplication")
        if(!mediaStorageDir.exists()&& !mediaStorageDir.mkdirs()){
            Log.d("ParseApplication","failed to create directory")
        }

        return File(mediaStorageDir.path + File.separator+fileName)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            Log.i("CUSTOMA","returned from camera")
            if(resultCode == AppCompatActivity.RESULT_OK) {
                Log.i("CUSTOMA","result ok")
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)

                ivPreview.setImageBitmap(takenImage)
            }else{
                Toast.makeText(requireContext(), "Picture wasn't taken",Toast.LENGTH_LONG).show()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

}