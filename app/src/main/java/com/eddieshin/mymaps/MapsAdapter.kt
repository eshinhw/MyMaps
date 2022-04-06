package com.eddieshin.mymaps

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eddieshin.mymaps.models.UserMap

// MapsAdapter inherits from RecyclerView.Adapter (becomes the child of RecyclerView.Adapter)
// RecyclerView.Adapter is parameterized by ViewHolder

/*
What are Parameterized Types (Genetic Programming...?) ?
Parameterized Types allow us to define a container by specifying what kind of things can go in this container
myContainer<Monkey> = new myContainer<Monkey>(); --> We can only put Monkeys in myContainer
myContainer<Undergraduate> = new myContainer<Undergraduate>(); --> myContainer only can contain Undergraduate
public class LinkedList<E> {
    // E for element becomes a genetic type throughout the rest of the class
    // E can be Monkey, Undergraduate, ... etc
    public void addFirst (String s) {
    } // without generic
    becomes
    public void addFirst (E obj) {
    }
    public String removeFirst() --> public E removeFirst()
    E[] storage = (E[]) new Object[size];
}
Constructors don't require <>
class Node <E> {
    E data;
    Node<E> next;
    public Node(E obc) {
        data = obj;
        next = null;
    }
}
 */

/*
6 STEPS OF IMPLEMENTING RECYCLERVIEW

1. Add RecyclerView AndroidX library to the Gradle build file
2. Define a model class to use as the data source
3. Add a RecyclerView to your activity to display the items
4. Create a custom row layout XML file to visualize the item
5. Create a RecyclerView.Adapter and ViewHolder to render the item
6. Bind the adapter to the data source to populate the RecyclerView

 */

private const val TAG = "MapsAdapter"
class MapsAdapter(val context: Context, val userMaps: List<UserMap>, val onClickListener: OnClickListener) : RecyclerView.Adapter<MapsAdapter.ViewHolder>() {
    // What does RecyclerView.Adapter<MapsAdapter.ViewHolder> mean?
    // Same concept of List<Person> : List is a template and each element of the template is Person type
    // RecyclerView.Adapter is parameterized/templatized by MapsAdapter.ViewHolder which is referring to the inner class we have inside MapsAdapter class
    interface OnClickListener {
        fun onItemClick(position: Int)
    }
    // ViewHolder class inherits from RecyclerView.ViewHolder
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) // dummy class?

    // onCreateViewHolder inflates the view inside of the ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_user_map, parent, false)
        return ViewHolder(view)
    }

    // grabs the data at position provided and bind it to the view located inside the ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userMap = userMaps[position]
        holder.itemView.setOnClickListener {
            Log.i(TAG, "Tapped on position $position")
            onClickListener.onItemClick(position)
        }
        val textViewTitle = holder.itemView.findViewById<TextView>(R.id.tvMapTitle) // get the id of the itemView
        textViewTitle.text = userMap.title
    }

    override fun getItemCount() = userMaps.size
}
