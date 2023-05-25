package com.example.travelerv2

import android.annotation.SuppressLint
import android.content.ClipData.Item
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class CustomAdapter(private val mList: List<Travel>,private val onItemClicked: (id:String) -> Unit) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.travel_item, parent, false)

        return ViewHolder(view,onItemClicked)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val ItemsViewModel = mList[position]


            // sets the text to the textview from our itemHolder class
            holder.tvPlaceName.text = ItemsViewModel.name
            holder.tvPlaceDiameter.text = ItemsViewModel.diameter.toString() + " KM"
            holder.travelId = ItemsViewModel.id
        val imageRef = FirebaseStorage.getInstance().getReference("images/${ItemsViewModel.file_name}")

        val localFile = File.createTempFile("tempImage","jpg")
        imageRef.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            val resizedBitMap = Bitmap.createScaledBitmap(bitmap,75,75,false)
            val roundedDrawable = RoundedBitmapDrawableFactory.create(holder.itemView.context.resources, resizedBitMap)
            roundedDrawable.cornerRadius = holder.itemView.context.resources.getDimension(R.dimen.img_radius)
            holder.imgTravel.setImageDrawable(roundedDrawable)
        }.addOnFailureListener{
            Log.d(TAG,"Failed to load images...")
        }


    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View, private val onItemClicked: (id:String) -> Unit) : RecyclerView.ViewHolder(ItemView),View.OnClickListener{

        val tvPlaceName: TextView= itemView.findViewById(R.id.tvPlaceName)
        val tvPlaceDiameter: TextView = itemView.findViewById(R.id.tvDiameter)
        val imgTravel: ImageView = itemView.findViewById(R.id.imgTravel)
        var travelId: String? = null
        init {
            itemView.setOnClickListener(this)

        }

        override fun onClick(p0: View?) {


            travelId?.let { onItemClicked(it) }
        }



    }
}