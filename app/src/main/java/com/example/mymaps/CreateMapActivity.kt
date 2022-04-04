package com.example.mymaps

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.mymaps.MainActivity.Companion.EXTRA_MAP_TITLE

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.mymaps.databinding.ActivityCreateMapBinding
import com.google.android.gms.maps.model.Marker
import com.google.android.material.snackbar.Snackbar

class CreateMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityCreateMapBinding

    private var markers : MutableList<Marker> = mutableListOf()

    companion object {
        private const val TAG = "CreateMapActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = intent.getStringExtra(EXTRA_MAP_TITLE)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        // we only execute the following code when mapFragment is not NULL
        mapFragment.view?.let {
            Snackbar.make(it, "Long press to add a marker!", Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", {})
                .setActionTextColor(ContextCompat.getColor(this, android.R.color.white))
                .show()
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnInfoWindowClickListener() { markerToDelete ->
            // onWindowClickListener - delete this marker
            // remove the marker from the list of markers
            markers.remove(markerToDelete)
            // remove the marker from the map
            markerToDelete.remove()
        }

        mMap.setOnMapLongClickListener() { latLng ->
            Log.i(TAG, "long click activated")
            Log.i(TAG, "New location: $latLng")
            // we need to get the marker name and description
            // create an alert dialogue for user input
            showAlertDialog(latLng)

        }

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    private fun showAlertDialog(latLng: LatLng) {
        val placeFormView = LayoutInflater.from(this).inflate(R.layout.dialog_create_place, null)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Create a marker")
            .setView(placeFormView)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("OK", null)
            .show()

        // we only want to add a market on the map when the user taps on OK button on dialog
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            // when working with user input data, we need to make sure they are valid.
            // error validataion & form handling
            val title = placeFormView.findViewById<EditText>(R.id.etPlaceName).text.toString()
            val description = placeFormView.findViewById<EditText>(R.id.etPlaceDescription).text.toString()

            if (title.trim().isNotEmpty() && description.trim().isNotEmpty()) {
                val marker = mMap.addMarker(MarkerOptions().position(latLng).title(title).snippet(description))
                marker?.let { markers.add(it) }
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Place must have non-empty title and description", Toast.LENGTH_LONG).show()
                dialog.dismiss()
                return@setOnClickListener
            }


        }

    }
}