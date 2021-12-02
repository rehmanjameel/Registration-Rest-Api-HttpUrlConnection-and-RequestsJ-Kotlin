package com.example.restapiregistration

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import org.json.JSONObject
import java.net.URL
import pk.codebase.requests.HttpError
import pk.codebase.requests.HttpHeaders
import pk.codebase.requests.HttpRequest
import pk.codebase.requests.HttpResponse


class HomeScreen : AppCompatActivity() {

    lateinit var imageView: ImageView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var imageUri: Uri
    val imageUrl = URL("http://192.168.100.36:8000/api/image/")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        imageView = findViewById(R.id.homeImageView)
        sharedPreferences = getSharedPreferences("getToken", Context.MODE_PRIVATE)

        getImagesFromServer()
    }

    private fun getImagesFromServer() {

        val token = sharedPreferences.getString("Token", "")

        val request = HttpRequest()
        request.setOnResponseListener { response ->
                if (response.code == HttpResponse.HTTP_OK) {
                    println(response.toJSONArray())
                    val images = response.toJSONArray().toString()
                    Glide.with(this).load(images).into(imageView)

                    /*val jsonResponse: JSONObject = response.toJSONObject()
                    jsonResponse.get("image")*/
                    Toast.makeText(this, response.text, Toast.LENGTH_LONG).show()
                    Log.e("Responseofimage", "${response.code}")
                    Log.e("Responseofimage", response.text)
            }
        }
        request.setOnErrorListener{
                // There was an error, deal with it
            Toast.makeText(this, "error", Toast.LENGTH_LONG).show()
        }
        val headers = HttpHeaders("Authorization", "Token $token")
        request.get("$imageUrl", headers)
    }
}