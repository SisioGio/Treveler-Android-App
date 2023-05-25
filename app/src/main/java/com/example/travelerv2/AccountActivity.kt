package com.example.travelerv2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AccountActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        // Get the user data from the Intent extras
        // Get the user data from the Intent extras
        val userId = intent.getStringExtra("userId")


        findViewById<TextView>(R.id.tvUserId).text = userId

        val btnAddTravel = findViewById<Button>(R.id.btnAddTravel)
        btnAddTravel.setOnClickListener{
            val intent = Intent(this,AddActivity::class.java)
            intent.putExtra("userId", userId);
            startActivity(intent)
        }


    }
}