package com.eddieshin.mymaps

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eddieshin.mymaps.models.Place
import com.eddieshin.mymaps.models.UserMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.*

/* Episode 6: Data Persistence

- Shared Preferences
- Local Files
    when the data we need to store is small, we can quickly save the data into the file and read the file when we open the app.
    However, if there are millions of data which must be stored, writing to a single file is very slow and inefficient.
    Not a perfect solution for all possible scenarios, but done is better than perfect.
    In Android environment, there is a dedicated location for reading and writing files.
- SQLite Database
- ORM

 */

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
        private const val FILE_NAME = "UserMaps.data"
    }

    private lateinit var userMaps : MutableList<UserMap>
    private lateinit var mapAdapter : MapsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val dataFromFile = deserializeUserMaps(this)

        userMaps = if (dataFromFile.isEmpty()) {
            generateSampleData().toMutableList()
        } else {
            dataFromFile.toMutableList()
        }

        // get the list of UserMap objects which contain different places under each UserMap

        // Set layout manager on the recycler view
        // Layout manager: responsible for telling the recyclerview how to layout the views on the screen
        // grid, stagger grid, vertical, horizontal or linear

        // In a member of a class, this refers to the current object of that class.

        rvMaps.layoutManager = LinearLayoutManager(this)

        // Set adapter
        // Adapter is responsible for taking the data (in our case, userMaps) and binding it to particular view in the recyclerView
        // userMaps, the second parameter of MapsAdapter, is the actual list of data created in MainActivity which is passed to the adaptor
        mapAdapter = MapsAdapter(this, userMaps, object: MapsAdapter.OnClickListener {
            override fun onItemClick(position: Int) {
                Log.i(TAG, "onItemClick $position")
                val intent = Intent(this@MainActivity, DisplayMapActivity::class.java)
                Log.i(TAG, "${userMaps[position]}")
                intent.putExtra(EXTRA_USER_MAP, userMaps[position])
                startActivity(intent)
            }

        })

        rvMaps.adapter = mapAdapter

        fabCreateMap.setOnClickListener() {
            Log.i(TAG, "add button clicked!")
            showAlertDialog()
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
            val userMap = data?.getSerializableExtra(EXTRA_USER_MAP) as UserMap
            Log.i(TAG, "onActivityResult with new map title ${userMap.title}")
            userMaps.add(userMap)
            serializeUserMaps(this, userMaps)
            mapAdapter.notifyDataSetChanged()

        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun serializeUserMaps(context: Context, userMaps: List<UserMap>) {
        Log.i(TAG, "serializeUserMaps")
        // Get FileOutputStream, pass it to ObjectOutputStream and write userMaps
        // It's possible because userMaps is serializable
        ObjectOutputStream(FileOutputStream(getDataFile(context))).use { it.writeObject(userMaps) }
    }

    private fun deserializeUserMaps(context: Context) : List<UserMap> {
        Log.i(TAG, "deserializeUserMaps")
        val dataFile = getDataFile(context)
        if (!dataFile.exists()) {
            Log.i(TAG, "dataFile doesn't exist")
            return emptyList()
        }
        ObjectInputStream(FileInputStream(dataFile)).use { return it.readObject() as List<UserMap>}
    }

    private fun getDataFile(context: Context) : File {
        Log.i(TAG, "Getting file from directory ${context.filesDir}")
        return File(context.filesDir, FILE_NAME)
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

    private fun showAlertDialog() {
        val mapFormView = LayoutInflater.from(this).inflate(R.layout.dialog_create_map, null)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Map Title")
            .setView(mapFormView)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("OK", null)
            .show()

        // we only want to add a market on the map when the user taps on OK button on dialog
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            // when working with user input data, we need to make sure they are valid.
            // error validation & form handling
            val title = mapFormView.findViewById<EditText>(R.id.etMapTitleName).text.toString()

            if (title.trim().isNotEmpty()) {
                // User has provided proper title
                val intent = Intent(this@MainActivity, CreateMapActivity::class.java)
                intent.putExtra(EXTRA_MAP_TITLE, title)
                startActivityForResult(intent, REQUEST_CODE)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Map must have non-empty title!", Toast.LENGTH_LONG).show()
                dialog.dismiss()
                return@setOnClickListener
            }


        }

    }


}