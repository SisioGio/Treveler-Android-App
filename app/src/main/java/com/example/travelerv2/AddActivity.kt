package com.example.travelerv2

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
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
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class AddActivity : AppCompatActivity() {
    private  lateinit var mDbRef: DatabaseReference
    var ImageUri : Uri? = null
    private lateinit var locationActivityResultLauncher: ActivityResultLauncher<Intent>
    private var selectedLat by Delegates.notNull<Double>()
    private var selectedLng by Delegates.notNull<Double>()
    lateinit var FileName : String
    private var  lat : Double? = null
    private var  lng : Double? = null


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//          Add travel to FireBase
        mDbRef = FirebaseDatabase.getInstance().reference
        setContentView(R.layout.activity_add_travel)
        // Define buttons
        val btnSaveTravel = findViewById<Button>(R.id.btnSaveTravel)

        // Get the user data from the Intent extras
        val userId = intent.getStringExtra("userId")

        // Map Button - Open map activity with current location + search functionality
        val btnMap = findViewById<ImageButton>(R.id.btnPickLocation)
        btnMap.setOnClickListener{
            val intent = Intent(this,MapActivity::class.java)
            resultLauncherLocation.launch(intent)
        }
        // Upload new image
        val btnUpload = findViewById<ImageView>(R.id.imageView2)
        btnUpload.setOnClickListener{
           selectImage()
        }



        btnSaveTravel.setOnClickListener{


            var placeName = findViewById<EditText>(R.id.etName).text.toString()
            var diameter =  findViewById<EditText>(R.id.etDiameter).text.toString()
            var description = findViewById<EditText>(R.id.etDescription).text.toString()


            uploadImage()


            var travelRef = mDbRef.child("users").child(userId!!).child("travels").push()
            travelRef.setValue(Travel(travelRef.key,placeName,description, diameter.toDoubleOrNull(),FileName,selectedLat,selectedLng))
            Toast.makeText(
                this@AddActivity,
                "Saved!",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(this,TravelsActivity::class.java)
            intent.putExtra("userId",userId)
            startActivity(intent)

        }



    }

    // Upload image into Firebase storage
    private fun uploadImage(){
        val formatter = SimpleDateFormat("YYYY_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()


        FileName = formatter.format(now)
        val storageRef = FirebaseStorage.getInstance().getReference("images/$FileName")
        storageRef.putFile(ImageUri!!).addOnSuccessListener {
            Toast.makeText(
                baseContext,
                "Image loaded!",
                Toast.LENGTH_SHORT,
            ).show()
        }.addOnFailureListener{
            Toast.makeText(
                baseContext,
                "Image not loaded!",
                Toast.LENGTH_SHORT,
            ).show()
        }

    }

    // Open image selection
    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        resultLauncher.launch(intent)


    }
    // Set image URI when image is selected
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->

        if(result.resultCode == Activity.RESULT_OK){
            val data: Intent? = result.data
            ImageUri = data?.data!!
            findViewById<ImageView>(R.id.imageView2).setImageURI(ImageUri)

        }
    }





    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    var resultLauncherLocation = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->

        if(result.resultCode == Activity.RESULT_OK){
            val data: Intent? = result.data
            selectedLat = data?.getDoubleExtra("selectedLat",0.00)!!
            selectedLng = data?.getDoubleExtra("selectedLng",0.00)!!


        }
    }

}