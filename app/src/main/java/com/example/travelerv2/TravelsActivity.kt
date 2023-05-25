package com.example.travelerv2

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.Intent.getIntent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class TravelsActivity : AppCompatActivity() {
    private  lateinit var mDbRef: DatabaseReference
    private var userId: String = ""
    val travelList: ArrayList<Travel> = ArrayList()
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_travels)

        // Get the user data from the Intent extras
        userId = intent.getStringExtra("userId")!!

        // Button - Go to AddTravel activity
        val btnAddTravel = findViewById<Button>(R.id.btnAddTravel)
        btnAddTravel.setOnClickListener{
            val intent = Intent(this,AddActivity::class.java)
            intent.putExtra("MODE", "NEW");
            intent.putExtra("userId", userId);
            startActivity(intent)
        }

            mDbRef = FirebaseDatabase.getInstance().reference
            var travelRef = mDbRef.child("users").child(userId).child("travels")

        travelRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                travelList.clear()
                for (travelSnapshot in snapshot.children) {
                    val travel = travelSnapshot.getValue(Travel::class.java)
                    Log.d(ContentValues.TAG, travel.toString())
                    travel?.let{
                        travelList.add(travel)
                    }
                }
                refreshData()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error condition
            }
        })


        refreshData()

        }


    // Refresh list of debtors
    @SuppressLint("Range")
    private fun refreshData() {
        val contentResolver = applicationContext.contentResolver


        val recyclerview = findViewById<RecyclerView>(R.id.rvTravels)

        recyclerview.layoutManager = LinearLayoutManager(this)


        val adapter = CustomAdapter(travelList) { id -> onListItemClick(id) }

        recyclerview.adapter = adapter



    }

// Add list item event listener onClick
@SuppressLint("MissingInflatedId", "Range")
private fun onListItemClick(id:String) {

    Log.d(ContentValues.TAG, "Clicked $id")

    val intent = Intent(this,UpdateActivity::class.java)
    intent.putExtra("travelId", id);
    intent.putExtra("MODE", "UPDATE");
    intent.putExtra("userId", userId);
    startActivity(intent)
}


    }


