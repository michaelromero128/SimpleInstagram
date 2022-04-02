package com.example.simpleinstagram

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.simpleinstagram.fragments.ComposeFragment
import com.example.simpleinstagram.fragments.HomeFragment
import com.example.simpleinstagram.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.parse.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentManager: FragmentManager = supportFragmentManager
        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnItemSelectedListener {
            item->
            var fragment: Fragment? = null

            when(item.itemId){
                R.id.action_home-> {
                    fragment = HomeFragment()
                }
                R.id.action_compose ->{
                    fragment = ComposeFragment()
                }
                R.id.action_profile ->{
                    fragment = ProfileFragment()
                }
            }
            if(fragment != null){
                fragmentManager.beginTransaction().replace(R.id.flContainer,fragment).commit()
            }

            true
        }

        findViewById<BottomNavigationView>(R.id.bottom_navigation).setSelectedItemId(R.id.action_compose)
    }
    fun submitPost(description: String, user:ParseUser,photoFile:File){
        val post = Post()
        post.setDescription(description)
        post.setUser(user)
        post.setImage(ParseFile(photoFile))
        post.saveInBackground{e ->
            if(e != null){
                e.printStackTrace()
            }else{
                Log.i("CUSTOMA","post saved")
                findViewById<EditText>(R.id.etDescription).text.clear()
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
            val fileProvider: Uri = FileProvider.getUriForFile(this, "com.codepath.fileprovider",photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            if(intent.resolveActivity(packageManager) != null){
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            Log.i("CUSTOMA","returned from camera")
            if(resultCode == RESULT_OK) {
                Log.i("CUSTOMA","result ok")
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                val ivPreview: ImageView = findViewById(R.id.ivPicture)
                ivPreview.setImageBitmap(takenImage)
            }else{
                Toast.makeText(this, "Picture wasn't taken",Toast.LENGTH_LONG).show()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
    fun getPhotoFileUri(fileName:String): File{
        val mediaStorageDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),"ParseApplication")
        if(!mediaStorageDir.exists()&& !mediaStorageDir.mkdirs()){
            Log.d("ParseApplication","failed to create directory")
        }

        return File(mediaStorageDir.path + File.separator+fileName)
    }

}