package com.example.restapiregistration

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restapiregistration.adapter.ImagesAdapter
import com.example.restapiregistration.model.User
import java.net.URL
import pk.codebase.requests.HttpHeaders
import pk.codebase.requests.HttpRequest
import pk.codebase.requests.HttpResponse


class HomeScreen : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    private val userModel = ArrayList<User>()
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: ImagesAdapter

    val imageUrl = URL("http://192.168.100.242:8000/api/image/")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        title = "Home"

        recyclerView = findViewById(R.id.imagesRecylerView)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)

        sharedPreferences = getSharedPreferences("getToken", Context.MODE_PRIVATE)

        getImagesFromServer()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    @SuppressLint("CommitPrefEdits")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.userProfile) {
            val intent = Intent(applicationContext, ProfileActivity::class.java)
            startActivity(intent)
        }

        if (id == R.id.addImages) {
            val intent = Intent(applicationContext, ImagesActivity::class.java)
            startActivity(intent)
        }

        if (id == R.id.logOutButton) {
            editor = sharedPreferences.edit()
            editor.clear()
            editor.commit()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun getImagesFromServer() {

        val token = sharedPreferences.getString("Token", "")

        val request = HttpRequest()
        request.setOnResponseListener { response ->
                if (response.code == HttpResponse.HTTP_OK) {
                    //Get the array
                    val jsonArray = response.toJSONArray()
                    for (i in 0 until jsonArray!!.length()) {

                        //Get the JasonArray indexes
                        val jsonObject = jsonArray.getJSONObject(i)
                        //get image from jsonObject
                        val image = jsonObject.getString("image")
                        //get userName from JsonObject
                        val userName = jsonObject.getString("user")

                        userModel.add(User(userName.toString(), image))
                        adapter = ImagesAdapter(applicationContext, userModel)
                        recyclerView.adapter = adapter
                        userModel.reverse()
                        Log.e("Images", image)
                        Log.e("UserName", userName.toString())

                    }
//                    println(response.toJSONArray())
//                    Toast.makeText(this, response.text, Toast.LENGTH_LONG).show()
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