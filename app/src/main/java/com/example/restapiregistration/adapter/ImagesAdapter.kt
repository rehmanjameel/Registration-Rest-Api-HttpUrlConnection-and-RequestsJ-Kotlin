package com.example.restapiregistration.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.restapiregistration.R
import com.example.restapiregistration.model.User
import com.google.gson.JsonObject
import org.json.JSONObject

class ImagesAdapter(val context: Context, private val userData: List<User>):
    RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_design, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = userData[position].userName
//        val jsonObject = JSONObject(userData[position].userImages)
        Glide.with(context)
            .load("http://192.168.100.242:8000"+userData[position].userImages)
            .into(holder.imageView)
        Log.e("ImageData", "image: ${userData[position].userImages}")
    }

    override fun getItemCount(): Int {
        return userData.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.homeImageView)
        val textView: TextView = itemView.findViewById(R.id.userNameImageTextView)
    }

}