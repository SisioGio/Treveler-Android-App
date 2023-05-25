package com.example.travelerv2

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var auth: FirebaseAuth
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val registrationButton = findViewById<TextView>(R.id.signup)
        registrationButton.setOnClickListener{
            var intent = getIntent()
            intent = Intent(this@LoginActivity,com.example.travelerv2.RegistrationActivity::class.java)
            startActivity(intent)
        }

        val loginButton = findViewById<Button>(R.id.signin)
        loginButton.setOnClickListener{
            var emailAddress = findViewById<EditText>(R.id.etEmail).text.toString()
            var password = findViewById<EditText>(R.id.etPassword).text.toString()
            auth.signInWithEmailAndPassword(emailAddress,password).addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    val user = auth.currentUser




                    Toast.makeText(
                        baseContext,
                        "Congrats! Logged in!",
                        Toast.LENGTH_SHORT,
                    ).show()
                    val intent = Intent(this,TravelsActivity::class.java)
                    // Pass the user data as extras to the Intent
                    if (user != null) {

                        intent.putExtra("userId", user.uid.toString());

                        intent.putExtra("email", user.email.toString());

                    }


                    startActivity(intent)
                } else {

                    Toast.makeText(
                        baseContext,
                        "Login failed",
                        Toast.LENGTH_SHORT,
                    ).show()
                }


            }
        }




    }
}