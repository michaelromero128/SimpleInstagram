package com.example.simpleinstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.parse.ParseUser

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        if(ParseUser.getCurrentUser() != null){
            goToMainActivity()
        }
        findViewById<Button>(R.id.btLogin).setOnClickListener{
            val username = findViewById<EditText>(R.id.etUsername).text.toString()
            val password = findViewById<EditText>(R.id.etPassword).text.toString()
            loginUser(username,password)
        }
        findViewById<Button>(R.id.btSignUp).setOnClickListener{
            val username = findViewById<EditText>(R.id.etUsername).text.toString()
            val password = findViewById<EditText>(R.id.etPassword).text.toString()
            signUpUser(username, password)
        }
    }
    private fun signUpUser(userName:String, password:String){
        val user = ParseUser()
        user.username = userName
        user.setPassword(password)
        user.signUpInBackground{ e->
            if(e == null){
               goToMainActivity()
            }else{
                e.printStackTrace()
            }
        }
    }

    private fun loginUser(username: String, password: String) {
        ParseUser.logInInBackground(username,password, ({user, e ->
            run {
                if (user != null) {
                    goToMainActivity()
                } else {
                    Log.i("CUSTOMA", "Bad user ")
                    e.printStackTrace()
                    Toast.makeText(this, "Error Logging in", Toast.LENGTH_LONG).show()
                }
            }
        }))
    }
    private fun goToMainActivity(){
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
    }
}