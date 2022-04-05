package com.eddieshin.mymaps

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.eddieshin.mymaps.MainActivity.Companion.EXTRA_MAP_TITLE
import com.eddieshin.mymaps.MainActivity.Companion.EXTRA_USER_MAP

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.eddieshin.mymaps.databinding.ActivityCreateMapBinding
import com.eddieshin.mymaps.models.Place
import com.eddieshin.mymaps.models.UserMap
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_create_map, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Check that 'item' is the save menu icon

        if (item.itemId == R.id.miSave) {
            Log.i(TAG, "Activated Save menu")
            if (markers.isEmpty()) {
                Toast.makeText(this, "There must be at least one marker on the map", Toast.LENGTH_LONG).show()
            }
            val places = markers.map { marker -> Place(marker.title.toString(), marker.snippet.toString(), marker.position.latitude, marker.position.longitude) }
            val userMap = intent.getStringExtra(EXTRA_MAP_TITLE)?.let { UserMap(it, places) }
            val data = Intent()
            data.putExtra(EXTRA_USER_MAP, userMap)
            setResult(Activity.RESULT_OK, data)
            finish()

            return true
        }
        return super.onOptionsItemSelected(item)
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
        val toronto = LatLng(43.6532, -79.3832)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(toronto, 10f))
        // zoom level between 1 and 21
        /*
        1: World
        5: Landmass/continent
        10: City
        15: Streets
        20: Buildings
         */
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