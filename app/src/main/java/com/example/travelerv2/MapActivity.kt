package com.example.travelerv2

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.ktx.widget.PlaceSelectionError
import com.google.android.libraries.places.ktx.widget.PlaceSelectionSuccess
import com.google.android.libraries.places.ktx.widget.placeSelectionEvents
import com.google.android.libraries.places.widget.AutocompleteSupportFragment

class MapActivity : AppCompatActivity(),OnMapReadyCallback {
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationPermissionGranted  : Boolean=false
    private lateinit var lastKnownLocation: Location
    private val DEFAULT_ZOOM = 15.0f
    private val defaultLocation = LatLng(41.902782,12.496366)
    private  lateinit var selectedPlace : LatLng


    override fun onCreate(savedInstanceState: Bundle?) {
        Places.initialize(this, BuildConfig.MAPS_API_KEY)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Get the user data from the Intent extras
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.NAME,
                Place.Field.ID,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
            )

        )


        // Listen to place selection events
        lifecycleScope.launchWhenCreated {
            autocompleteFragment.placeSelectionEvents().collect { event ->
                when (event) {
                    is PlaceSelectionSuccess -> {
                        val place = event.place
                        selectedPlace = place.latLng
                        Log.d(TAG, "Selected place data (" + place.latLng.latitude +" ," + place.latLng.longitude +")")
//                        responseView.text = StringUtil.stringifyAutocompleteWidget(place, false)
                        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            place.latLng, DEFAULT_ZOOM.toFloat()))



                        // Add a marker
                        val markerOptions = MarkerOptions().position(place.latLng).title("Marker Title")
                        googleMap.addMarker(markerOptions)

                    }

                    is PlaceSelectionError -> Toast.makeText(
                        this@MapActivity,
                        "Failed to get place '${event.status.statusMessage}'",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


        }

        val btnConfirmLocation = findViewById<Button>(R.id.btnConfirmLocation)
        btnConfirmLocation.setOnClickListener{
            val intent = Intent()
            intent.putExtra("selectedLat",selectedPlace.latitude)
            intent.putExtra("selectedLng",selectedPlace.longitude)
            setResult(Activity.RESULT_OK,intent)
            finish()

        }
    }
    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        updateLocationUI()
    }


    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1001
    }
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        updateLocationUI()
        // Configure map settings
// Get the current location of the device and set the position of the map.
        getDeviceLocation()


    }

    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        if (googleMap == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                googleMap?.isMyLocationEnabled = true
                googleMap?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                googleMap?.isMyLocationEnabled = false
                googleMap?.uiSettings?.isMyLocationButtonEnabled = false

                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }


    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {

        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {

//          Check if existing travel position was passed as argument
            var hasLat = intent.hasExtra("lat")
            var hasLng = intent.hasExtra("lng")
            if(hasLat && hasLng){
                var inputLat :Double?= intent.getDoubleExtra("lat",0.0)
                var inputLng :Double?= intent.getDoubleExtra("lng",0.0)
                var  initialLocation = LatLng(inputLat!!,inputLng!!)
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    initialLocation, DEFAULT_ZOOM.toFloat()))



                // Add a marker
                val markerOptions = MarkerOptions().position(initialLocation).title("Marker Title")
                googleMap.addMarker(markerOptions)
                selectedPlace = initialLocation



            } else if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            var  initialLocation = LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
                            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                initialLocation, DEFAULT_ZOOM.toFloat()))



                            // Add a marker
                            val markerOptions = MarkerOptions().position(initialLocation).title("Marker Title")
                            googleMap.addMarker(markerOptions)
                            selectedPlace = initialLocation

                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        googleMap?.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat()))
                        googleMap?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

}