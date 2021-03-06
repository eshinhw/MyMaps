package com.eddieshin.mymaps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.eddieshin.mymaps.MainActivity.Companion.EXTRA_USER_MAP

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.eddieshin.mymaps.databinding.ActivityDisplayMapBinding
import com.eddieshin.mymaps.models.UserMap
import com.google.android.gms.maps.model.LatLngBounds


class DisplayMapActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val TAG = "DisplayMapActivity"
    }

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityDisplayMapBinding
    private lateinit var userMap: UserMap



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDisplayMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userMap = intent.getSerializableExtra(EXTRA_USER_MAP) as UserMap

        // update the header of DisplayMapActivity
        supportActionBar?.title = userMap.title

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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

        Log.i(TAG, "user map to render: ${userMap.title}")
        Log.i(TAG, "userMap.places = ${userMap.places}")

        val boundsBuilder = LatLngBounds.Builder()

        for (place in userMap.places) {
            Log.i(TAG, "place title: ${place.title}")
            Log.i(TAG, "place description: ${place.description}")
            Log.i(TAG, "place latitude: ${place.latitude}")
            Log.i(TAG, "place longitude: ${place.longitude}")

            val latLng = LatLng(place.latitude, place.longitude)
            boundsBuilder.include(latLng)
            mMap.addMarker(MarkerOptions().position(latLng).title(place.title).snippet(place.description))

        }
        // There are many variations of working with camera ; moveCamera, animateCamera
        // mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 1000, 1000, 0))
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 1000, 1000, 0))

    }
}