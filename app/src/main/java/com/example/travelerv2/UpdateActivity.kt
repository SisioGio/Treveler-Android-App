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

class UpdateActivity : AppCompatActivity() {
    private  lateinit var mDbRef: DatabaseReference
    var ImageUri : Uri? = null
    private lateinit var locationActivityResultLauncher: ActivityResultLauncher<Intent>
    private var selectedLat by Delegates.notNull<Double>()
    private var selectedLng by Delegates.notNull<Double>()
    lateinit var FileName : String
    private var  lat : Double? = null
    private var  lng : Double? = null
    private var MODE :String? = ""
    private lateinit var TravelObj : Travel

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//          Add travel to FireBase
        mDbRef = FirebaseDatabase.getInstance().reference
        setContentView(R.layout.activity_update_travel)
        // Define buttons

        val btnUpdateTravel = findViewById<Button>(R.id.btnUpdateTravel)
        val btnDeleteTravel = findViewById<Button>(R.id.btnDeleteTravel)

        // Get the user data from the Intent extras
        val userId = intent.getStringExtra("userId")
        val travelId = intent.getStringExtra("travelId")

        var travelRef = mDbRef.child("users").child(userId!!).child("travels").child(travelId!!)
        travelRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    TravelObj = snapshot.getValue(Travel::class.java)!!
                    FileName = TravelObj.file_name!!
                    selectedLng = TravelObj?.lng!!
                    selectedLat = TravelObj?.lat!!
                    // Set text values
                    findViewById<EditText>(R.id.etName).setText(TravelObj.name)
                    findViewById<EditText>(R.id.etDiameter).setText(String.format("%.0f",TravelObj.diameter))
                    findViewById<EditText>(R.id.etDescription).setText(TravelObj.descrition)

                    // Download image and set it to the ImageView as bitmap
                    val imageRef = FirebaseStorage.getInstance().getReference("images/${TravelObj.file_name}")
                    val localFile = File.createTempFile("tempImage", "jpg")
                    imageRef.getFile(localFile).addOnSuccessListener {
                        val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)

                        val roundedDrawable =
                            RoundedBitmapDrawableFactory.create(this@UpdateActivity.resources, bitmap)
                        roundedDrawable.cornerRadius = this@UpdateActivity.resources.getDimension(R.dimen.img_radius)
                        findViewById<ImageView>(R.id.imageView2).setImageDrawable(roundedDrawable)
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@UpdateActivity,
                        "Error while reading data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            )


        val btnMap = findViewById<ImageButton>(R.id.btnPickLocation)
        btnMap.setOnClickListener{
            val intent = Intent(this,MapActivity::class.java)

            intent.putExtra("lat",TravelObj.lat)
            intent.putExtra("lng",TravelObj.lng)
            resultLauncherLocation.launch(intent)
        }

        val btnUpload = findViewById<ImageView>(R.id.imageView2)
        btnUpload.setOnClickListener{
           selectImage()
        }


        btnDeleteTravel.setOnClickListener{
            var travelRef = mDbRef.child("users").child(userId!!).child("travels").child(travelId!!)
            travelRef.removeValue().addOnSuccessListener {
                Toast.makeText(
                    this@UpdateActivity,
                    "Travel deleted!",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(this,TravelsActivity::class.java)

                intent.putExtra("userId",userId)

                startActivity(intent)
            }.addOnFailureListener{exception->
                Toast.makeText(
                    this@UpdateActivity,
                    "Error",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d(ContentValues.TAG, "Exception: ${exception.message}")
            }
        }


        btnUpdateTravel.setOnClickListener{
            var placeName = findViewById<EditText>(R.id.etName).text.toString()
            var diameter =  findViewById<EditText>(R.id.etDiameter).text.toString()
            var description = findViewById<EditText>(R.id.etDescription).text.toString()

            if(ImageUri != null){
                uploadImage()
            }



            travelRef.setValue(Travel(travelRef.key,placeName,description, diameter.toDoubleOrNull(),FileName,selectedLat,selectedLng))
            Toast.makeText(
                this@UpdateActivity,
                "Updated",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(this,TravelsActivity::class.java)
            intent.putExtra("userId",userId)
            startActivity(intent)

        }
    }

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
    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        resultLauncher.launch(intent)


    }

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