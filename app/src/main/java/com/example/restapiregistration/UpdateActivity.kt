package com.example.restapiregistration

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection

class UpdateActivity : AppCompatActivity() {

    private val UPDATE_URL = URL("http://192.168.100.36:8000/api/profile/")
    lateinit var sharedPreferences: SharedPreferences

    lateinit var firstName: EditText
    lateinit var lastName: EditText
//    lateinit var editEmail: EditText
    lateinit var editDoB: TextView
    lateinit var editMobile: EditText
    lateinit var editGender: EditText
    lateinit var editPassword: EditText
    lateinit var updateButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        sharedPreferences = getSharedPreferences("getToken", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("Token", "")
        Log.e("token3", "$token")

        firstName = findViewById(R.id.editFirstNameET)
        lastName = findViewById(R.id.editLastNameET)
//        editEmail = findViewById(R.id.editEmailET)
        editDoB = findViewById(R.id.editDoBET)
        editMobile = findViewById(R.id.editMobileET)
        editGender = findViewById(R.id.editGenderET)
        editPassword = findViewById(R.id.editPasswordET)
        updateButton = findViewById(R.id.updateButtonId)

        /*val first_name = sharedPreferences.getString("first_name", "")
        val last_name = sharedPreferences.getString("last_name", "")
        val date_of_birth = sharedPreferences.getString("date_of_birth", "")
        val email = sharedPreferences.getString("email", "")
        val gender = sharedPreferences.getString("gender", "")
        val mobile = sharedPreferences.getString("mobile", "")*/

        val first_name = intent.getStringExtra("first_name")
        val last_name = intent.getStringExtra("last_name")
        val date_of_birth = intent.getStringExtra("date_of_birth")
//        val email = intent.getStringExtra("email")
        val gender = intent.getStringExtra("gender")
        val mobile = intent.getStringExtra("mobile")
//        val password = intent.getStringExtra("password")

        Log.d("Profile Data", "$first_name")

        firstName.setText("$first_name")
        lastName.setText("$last_name")
        editDoB.text = date_of_birth
//        editEmail.setText("$email")
        editMobile.setText("$mobile")
        editGender.setText("$gender")
//        editPassword.setText("$password")

        updateButton.setOnClickListener {
            putMethod()
        }

        editDoB.setOnClickListener {
            dateOfBirthDialogue()
        }

    }

    private fun putMethod() {
        val token = sharedPreferences.getString("Token", "")
        val userName = sharedPreferences.getString("UserName", "")
        Log.d("Profile userName", "$userName")

        val json = JSONObject()
        try {
            json.put("username", "$userName")               //Username is required for verification
            json.put("first_name", "${firstName.text}")
            json.put("last_name", "${lastName.text}")
            json.put("date_of_birth", "${editDoB.text}")
            //json.put("email", "${editEmail.text}")                    //Email cannot change
            json.put("gender", "${editGender.text}")
            json.put("mobile", "${editMobile.text}")
            json.put("password", "${editPassword.text}")

            GlobalScope.launch(Dispatchers.IO) {
                val httpURLConnection = UPDATE_URL.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "PUT"
                httpURLConnection.setRequestProperty("Content-Type", "application/json") // The format of the content we're sending to the server
                httpURLConnection.setRequestProperty("Accept", "application/json") // The format of response we want to get from the server
                httpURLConnection.setRequestProperty("Authorization", "Token $token")   //Token required for authentication
                httpURLConnection.doInput = true
                httpURLConnection.doOutput = false
                val outputStreamWriter = OutputStreamWriter(httpURLConnection.outputStream)
                outputStreamWriter.write(json.toString())
                outputStreamWriter.flush()

                // Check if the connection is successful
                val responseCode = httpURLConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = httpURLConnection.inputStream.bufferedReader()
                        .use { it.readText() }  // defaults to UTF-8
                    withContext(Dispatchers.Main) {

                        Log.e("Respose Code00", responseCode.toString())
                        // Convert raw JSON to pretty JSON using GSON library
                        val gson = GsonBuilder().setPrettyPrinting().create()
                        val prettyJson = gson.toJson(JsonParser.parseString(response))
                        Log.d("Pretty Printed JSON :", prettyJson)

                        // Open DetailsActivity with the results
                        val intent = Intent(applicationContext, ProfileActivity::class.java)
                        //intent.putExtra("json_results", prettyJson)
                        startActivity(intent)
                        finish()

                    }
                } else {
                    Log.e("HTTPURLCONNECTION_ERROR", responseCode.toString())
                }
            }
        }catch (e: Exception) {
            Log.e("HttpUrlConnection Error", e.toString())
        }

    }

    private fun dateOfBirthDialogue() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            val selectedDate = "$selectedYear-${selectedMonth+1}-$selectedDayOfMonth"
            editDoB.text = selectedDate
        },year, month, day).show()
    }
}