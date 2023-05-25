package com.example.travelerv2

import android.content.ContentValues.TAG
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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class RegistrationActivity : AppCompatActivity() {
    private  lateinit var mDbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var auth: FirebaseAuth
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrion)

        val loginButton = findViewById<TextView>(R.id.signin)
        loginButton.setOnClickListener{
            var intent = getIntent()
            intent = Intent(this@RegistrationActivity,com.example.travelerv2.LoginActivity::class.java)
            startActivity(intent)
        }

        val registrationButton = findViewById<Button>(R.id.signup)
        registrationButton.setOnClickListener{
            val email = findViewById<EditText>(R.id.etEmail).text.toString()
            val password = findViewById<EditText>(R.id.etPassword).text.toString()
            val repeatPassword = findViewById<EditText>(R.id.etRepeatPassword).text.toString()
            if(email != null && password != null && repeatPassword != null){
                    if( password == repeatPassword){
                        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this){task->
                            if(task.isSuccessful){
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success")
                                val user = auth.currentUser
                                addUserToDatabase(email,user?.uid!!)

                                Toast.makeText(
                                    baseContext,
                                    "Congrats! Account created!",
                                    Toast.LENGTH_SHORT,
                                ).show()
                                val intent = Intent(this,LoginActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    } else {
                        Toast.makeText(
                            baseContext,
                            "Passwords do not match",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
            } else {
                Toast.makeText(
                    baseContext,
                    "All fields are mandatory",
                    Toast.LENGTH_SHORT,
                ).show()
            }

        }

    }

    private fun addUserToDatabase(email:String,uid:String){
        mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("users").child(uid).setValue(User(email,uid))
    }
}