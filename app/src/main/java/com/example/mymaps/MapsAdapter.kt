package com.example.mymaps

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mymaps.models.UserMap

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

private const val TAG = "MapsAdapter"
class MapsAdapter(val context: Context, val userMaps: List<UserMap>, val onClickListener: OnClickListener) : RecyclerView.Adapter<MapsAdapter.ViewHolder>() {

    interface OnClickListener {
        fun onItemClick(position: Int)
    }
    // ViewHolder class inherits from RecyclerView.ViewHolder
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // onCreateViewHolder inflates the view inside of the ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    // grabs the data at position provided and bind it to the view located inside the ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userMap = userMaps[position]
        holder.itemView.setOnClickListener {
            Log.i(TAG, "Tapped on position $position")
            onClickListener.onItemClick(position)
        }
        val textViewTitle = holder.itemView.findViewById<TextView>(android.R.id.text1) // get the id of the itemView
        textViewTitle.text = userMap.title
    }

    override fun getItemCount() = userMaps.size
}
