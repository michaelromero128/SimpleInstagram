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
import com.parse.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var posts:MutableList<Post>;
    var gotPosts: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btSubmit).setOnClickListener{
            val description = findViewById<EditText>(R.id.etDescription).text.toString()
            if(photoFile != null) {
                submitPost(description, ParseUser.getCurrentUser(), photoFile!!)
            }else{
                Log.e("CUSTOMA","Didn't take photo")
            }
        }
        findViewById<Button>(R.id.btTakePicture).setOnClickListener{
            onLaunchCamera()

        }
        findViewById<Button>(R.id.btLogOut).setOnClickListener{
            ParseUser.logOut();
            finish();
        }

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
    fun queryPosts(){
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        query.include(Post.KEY_USER)
        query.findInBackground(object: FindCallback<Post>{
            override fun done(objects: MutableList<Post>?, e: ParseException?) {
                if(e != null){
                    Log.e("CUSTOMA","Error fetching posts")
                }else{
                    posts = objects!!;
                    gotPosts = true;
                }
            }

        })
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