package com.example.restapiregistration

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import org.json.JSONObject


class ProfileActivity : AppCompatActivity() {
    private val URL_BASE = URL("http://192.168.100.242:8000/api/profile/")

    lateinit var sharedPreferences: SharedPreferences
    //lateinit var editor: SharedPreferences.Editor

    lateinit var firstName: TextView
    lateinit var lastName: TextView
    lateinit var userName: TextView
    lateinit var dateOfBirth: TextView
    lateinit var userMobile: TextView
    lateinit var userEmail: TextView
    lateinit var userGender: TextView
//    lateinit var editButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        title = "Profile"

        val actionBar = supportActionBar

        // showing the back button in action bar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        //Get token from sharedpreferences
        sharedPreferences = getSharedPreferences("getToken", Context.MODE_PRIVATE)

        firstName = findViewById(R.id.firstNameEditText)
        lastName = findViewById(R.id.lastNameEditText)
        userName = findViewById(R.id.userNameEditText)
        dateOfBirth = findViewById(R.id.dobEditText)
        userMobile = findViewById(R.id.mobilEditText)
        userEmail = findViewById(R.id.emailEditText)
        userGender = findViewById(R.id.genderEditText)
//        editButton = findViewById(R.id.editButtonId)
        /*val profile = intent.getStringExtra("json_results")
        profileTextView.text = profile*/

        getMethod()

        /*editButton.setOnClickListener {
            editButtonClick()
        }*/
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.profile_menu, menu)
        return true
    }

    @SuppressLint("CommitPrefEdits")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.updateUser) {
            //        val userPassword = sharedPreferences.getString("Password", "")
            val intent = Intent(applicationContext, UpdateActivity::class.java).apply {
                putExtra("first_name", "${firstName.text}")
                putExtra("last_name", "${lastName.text}")
//            putExtra("email", "${userEmail.text}")
                putExtra("mobile", "${userMobile.text}")
                putExtra("gender", "${userGender.text}")
                putExtra("date_of_birth", "${dateOfBirth.text}")
//            putExtra("password", "$userPassword")
//            Log.d("UserPassword: ", userPassword.toString())
            }
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("CommitPrefEdits")
    private fun getMethod() {
        val token = sharedPreferences.getString("Token", "")

        GlobalScope.launch(Dispatchers.IO) {

            val httpURLConnection = URL_BASE.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "GET"
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json") // The format of response we want to get from the server
            httpURLConnection.setRequestProperty("Authorization", "Token $token")
            httpURLConnection.doInput = true
            httpURLConnection.doOutput = false

            // Check if the connection is successful
            val responseCode = httpURLConnection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = httpURLConnection.inputStream.bufferedReader()
                    .use { it.readText() }  // defaults to UTF-8

                val jsonObject = JSONObject(response)

                firstName.text = jsonObject.getString("first_name")
                lastName.text = jsonObject.getString("last_name")
                userName.text = jsonObject.getString("username")
                dateOfBirth.text = jsonObject.getString("date_of_birth")
                userMobile.text = jsonObject.getString("mobile")
                userEmail.text = jsonObject.getString("email")
                userGender.text = jsonObject.getString("gender")
                //val userPassword = jsonObject.getString("password")

                //Saving values in shared preferences for updating
                /*editor = sharedPreferences.edit()
                editor.putString("first_name", firstName.toString())
                editor.putString("last_name", lastName.toString())
                editor.putString("date_of_birth", dateOfBirth.toString())
                editor.putString("mobile", userMobile.toString())
                editor.putString("email", userEmail.toString())
                editor.putString("gender", userGender.toString())
                editor.apply()*/

                withContext(Dispatchers.Main) {
                    // Convert raw JSON to pretty JSON using GSON library
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(JsonParser.parseString(response))
                    //profileTextView.text = prettyJson
                    Log.d("Pretty Printed JSON :", prettyJson)
                }
            } else {
                Log.e("HTTPURLCONNECTION_ERROR", responseCode.toString())
            }
        }
    }

   /* private fun editButtonClick() {
//        val userPassword = sharedPreferences.getString("Password", "")
        val intent = Intent(applicationContext, UpdateActivity::class.java).apply {
            putExtra("first_name", "${firstName.text}")
            putExtra("last_name", "${lastName.text}")
//            putExtra("email", "${userEmail.text}")
            putExtra("mobile", "${userMobile.text}")
            putExtra("gender", "${userGender.text}")
            putExtra("date_of_birth", "${dateOfBirth.text}")
//            putExtra("password", "$userPassword")
//            Log.d("UserPassword: ", userPassword.toString())
        }
        startActivity(intent)
        finish()
    }*/
}