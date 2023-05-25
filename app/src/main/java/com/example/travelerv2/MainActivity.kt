package com.example.travelerv2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var intent = getIntent()
        intent = Intent(this@MainActivity,com.example.travelerv2.LoginActivity::class.java)
        startActivity(intent)
        val registrationButton = findViewById<Button>(R.id.signup)
        registrationButton.setOnClickListener{
            var intent = getIntent()
            intent = Intent(this@MainActivity,com.example.travelerv2.RegistrationActivity::class.java)
            startActivity(intent)
        }

        val loginButton = findViewById<Button>(R.id.signin)
        loginButton.setOnClickListener{
            var intent = getIntent()
            intent = Intent(this@MainActivity,com.example.travelerv2.LoginActivity::class.java)
            startActivity(intent)
        }

    }
}