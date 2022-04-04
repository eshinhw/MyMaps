package com.example.mymaps

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymaps.models.Place
import com.example.mymaps.models.UserMap
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    private val rvMaps : RecyclerView by lazy {
        findViewById(R.id.rvMaps)
    }
    private val fabCreateMap : FloatingActionButton by lazy {
        findViewById(R.id.fabCreateMap)
    }

    companion object {
        const val EXTRA_USER_MAP = "EXTRA_USER_MAP"
        const val EXTRA_MAP_TITLE = "EXTRA_MAP_TITLE"
        private const val TAG = "MainActivity"
        private const val REQUEST_CODE = 200
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get the list of UserMap objects which contain different places under each UserMap
        val userMaps = generateSampleData()

        // Set layout manager on the recycler view
        // Layout manager: responsible for telling the recyclerview how to layout the views on the screen
        // grid, stagger grid, vertical, horizontal or linear

        // In a member of a class, this refers to the current object of that class.

        rvMaps.layoutManager = LinearLayoutManager(this)

        // Set adapter
        // Adapter is responsible for taking the data (in our case, userMaps) and binding it to particular view in the recyclerView
        // userMaps, the second parameter of MapsAdapter, is the actual list of data created in MainActivity which is passed to the adaptor
        rvMaps.adapter = MapsAdapter(this, userMaps, object: MapsAdapter.OnClickListener {
            override fun onItemClick(position: Int) {
                Log.i(TAG, "onItemClick $position")
                val intent = Intent(this@MainActivity, DisplayMapActivity::class.java)
                Log.i(TAG, "${userMaps[position]}")
                intent.putExtra(EXTRA_USER_MAP, userMaps[position])
                startActivity(intent)
            }

        })

        fabCreateMap.setOnClickListener() {
            Log.i(TAG, "add button clicked!")
            val intent = Intent(this@MainActivity, CreateMapActivity::class.java)
            intent.putExtra(EXTRA_MAP_TITLE, "new map title")
            startActivityForResult(intent, REQUEST_CODE)
        }


        // When user taps on view in RV, navigate to new activity
        /* Intent is a powerful concept within the Android universe.
        An intent is a message that can be thought of as a request that is given to either an activity within your own app,
        an external application, or a built-in Android service.
        Think of an intent as a way for an Activity to communicate with the outside Android world.
        - Take the user to another screen (activity) within your application.
        Intent is a core part of user flows in Android development.
        Explicit Intents: used to launch other activities within your application.
        We first need to create a class that we will navigate to.
         */
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Get new map data from the data
            // data?.getSerializableExtra()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun generateSampleData(): List<UserMap> {
        return listOf(
            UserMap(
                "Memories from University",
                listOf(
                    Place("Branner Hall", "Best dorm at Stanford", 37.426, -122.163),
                    Place("Gates CS building", "Many long nights in this basement", 37.430, -122.173),
                    Place("Pinkberry", "First date with my wife", 37.444, -122.170)
                )
            ),
            UserMap("January vacation planning!",
                listOf(
                    Place("Tokyo", "Overnight layover", 35.67, 139.65),
                    Place("Ranchi", "Family visit + wedding!", 23.34, 85.31),
                    Place("Singapore", "Inspired by \"Crazy Rich Asians\"", 1.35, 103.82)
                )),
            UserMap("Singapore travel itinerary",
                listOf(
                    Place("Gardens by the Bay", "Amazing urban nature park", 1.282, 103.864),
                    Place("Jurong Bird Park", "Family-friendly park with many varieties of birds", 1.319, 103.706),
                    Place("Sentosa", "Island resort with panoramic views", 1.249, 103.830),
                    Place("Botanic Gardens", "One of the world's greatest tropical gardens", 1.3138, 103.8159)
                )
            ),
            UserMap("My favorite places in the Midwest",
                listOf(
                    Place("Chicago", "Urban center of the midwest, the \"Windy City\"", 41.878, -87.630),
                    Place("Rochester, Michigan", "The best of Detroit suburbia", 42.681, -83.134),
                    Place("Mackinaw City", "The entrance into the Upper Peninsula", 45.777, -84.727),
                    Place("Michigan State University", "Home to the Spartans", 42.701, -84.482),
                    Place("University of Michigan", "Home to the Wolverines", 42.278, -83.738)
                )
            ),
            UserMap("Restaurants to try",
                listOf(
                    Place("Champ's Diner", "Retro diner in Brooklyn", 40.709, -73.941),
                    Place("Althea", "Chicago upscale dining with an amazing view", 41.895, -87.625),
                    Place("Shizen", "Elegant sushi in San Francisco", 37.768, -122.422),
                    Place("Citizen Eatery", "Bright cafe in Austin with a pink rabbit", 30.322, -97.739),
                    Place("Kati Thai", "Authentic Portland Thai food, served with love", 45.505, -122.635)
                )
            )
        )
    }


}