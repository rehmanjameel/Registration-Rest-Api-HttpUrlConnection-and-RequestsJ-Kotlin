package com.example.restapiregistration

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private val URL_BASE = URL("http://192.168.100.36:8000/api/login/")
    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

//    private val appGlobals: AppGlobals = AppGlobals()
    //private val request = HttpRequest()

    lateinit var loginUser: EditText
    lateinit var loginPassword: EditText
    lateinit var loginButton: Button
    lateinit var signUpText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences("getToken", MODE_PRIVATE)

        loginUser = findViewById(R.id.loginUserNameEditText)
        loginPassword = findViewById(R.id.loginPasswordEditText)
        loginButton = findViewById(R.id.loginButtonId)
        signUpText = findViewById(R.id.signUpTextView)

        loginButton.setOnClickListener {
            Toast.makeText(applicationContext, "clicked", Toast.LENGTH_SHORT).show()
            userLogin()
        }

        signUpText.setOnClickListener {
            moveToSignup()
        }
    }

    @SuppressLint("CommitPrefEdits")
    private fun userLogin() {

        if (loginUser.text.toString() == "" && loginPassword.text.toString() == "") {
            loginUser.error = "User name required"
            loginPassword.error = "Password required"
        } else if (loginUser.text.toString() == "") {
            loginUser.error = "User name required"
        } else if (loginPassword.text.toString() == "") {
            loginPassword.error = "Password required"
        } else {

            val json = JSONObject()
            try {
                json.put("username", loginUser.text.toString())
                json.put("password", loginPassword.text.toString())
                Log.e("Try", "After this method")

                //val jsonObjectString = json.toString()

                GlobalScope.launch(Dispatchers.IO) {
                    val httpURLConnection = URL_BASE.openConnection() as HttpURLConnection
                    httpURLConnection.requestMethod = "POST"
                    httpURLConnection.setRequestProperty("Content-Type", "application/json") // The format of the content we're sending to the server
                    httpURLConnection.setRequestProperty("Accept", "application/json") // The format of response we want to get from the server
                    httpURLConnection.doInput = true
                    httpURLConnection.doOutput = true

                    // Send the JSON we created
                    val outputStreamWriter = OutputStreamWriter(httpURLConnection.outputStream)
                    outputStreamWriter.write(json.toString())
                    outputStreamWriter.flush()

                    // Check if the connection is successful
                    val responseCode = httpURLConnection.responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val response = httpURLConnection.inputStream
                            .bufferedReader().use { it.readText() }  // defaults to UTF-8
                        Log.d("Json", response)
                        val jsonObj = JSONObject(response)
                        val accessToken = jsonObj.getString("token")
                        val userName = jsonObj.getString("username")
//                        val userPassword = jsonObj.getString("password")
                        editor = sharedPreferences.edit()
                        //appGlobals.saveDataToSharedPreferences("Token", accessToken)
                        editor.putString("Token", accessToken)
//                        editor.putString("Password", userPassword.toString())
                        editor.putString("UserName", userName)
                        editor.apply()
                        Log.d("Json token is:", accessToken)
                        Log.d("Json userName is:", userName)

                        withContext(Dispatchers.Main) {

                            // Convert raw JSON to pretty JSON using GSON library
                            /*val gson = GsonBuilder().setPrettyPrinting().create()
                            val prettyJson = gson.toJson(JsonParser.parseString(response))
                            Log.d("Pretty Printed JSON :", prettyJson)*/

                            // Open ProfileActivity with the results
                            val intent = Intent(applicationContext, HomeScreen::class.java)
                            //intent.putExtra("json_results", prettyJson)
                            startActivity(intent)
                        }
                    } else {
                        Log.e("HTTPURLCONNECTION_ERROR", responseCode.toString())
                    }
                }
            } catch (e: JSONException) {
                Log.e("Json Login: ", e.toString())
            }

        }

        //** Block of RequestsJ API method **//
      /*  else {
            request.setOnResponseListener { response ->
                if (response.code == HttpResponse.HTTP_OK) {
                    print("Register: ${response.toJSONObject()}")
                    Toast.makeText(applicationContext, "response.code ${response.code}", Toast.LENGTH_LONG).show()

                    loginUser.setText("")
                    loginPassword.setText("")

                    val intent = Intent(applicationContext, ProfileActivity::class.java)
                    startActivity(intent)
                    finish()
                } else if (response.code != HttpResponse.HTTP_OK){
                    println("response.text ${response.text}")
                    Toast.makeText(this, "Error ${response.code}", Toast.LENGTH_SHORT).show()
                }
            }Main
            request.setOnErrorListener {
                println("Not registered: $it")
            }*/

            //request.post(URL_BASE, json)
        //}
    }

    private fun moveToSignup() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}