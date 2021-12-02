package com.example.restapiregistration

import android.R.attr
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import pk.codebase.requests.HttpRequest
import pk.codebase.requests.HttpResponse
import java.lang.Exception
import pk.codebase.requests.HttpError
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.net.ssl.HttpsURLConnection
import java.io.IOException

import java.net.MalformedURLException

import java.io.InputStream

import android.R.attr.path

import java.util.HashMap

import java.io.DataOutputStream





class MainActivity : AppCompatActivity() {

    private val URL_BASE = "http://192.168.100.242:8000/api/register/"
    private val request = HttpRequest()

    lateinit var userName: EditText
    lateinit var firstName: EditText
    lateinit var lastName: EditText
    lateinit var email: EditText
    lateinit var dateOfBirth: TextView
    lateinit var userMobile: EditText
    lateinit var gender: EditText
    lateinit var password: EditText
    lateinit var registerButton: Button
    lateinit var logInText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "Register"

        userName = findViewById(R.id.userNameEditText)
        firstName = findViewById(R.id.firstNameEditText)
        lastName = findViewById(R.id.lastNameEditText)
        email = findViewById(R.id.emailEditText)
        dateOfBirth = findViewById(R.id.dobEditText)
        userMobile = findViewById(R.id.mobilEditText)
        gender = findViewById(R.id.genderEditText)
        password = findViewById(R.id.passwordEditText)
        registerButton = findViewById(R.id.registerButtonId)
        logInText = findViewById(R.id.logInTextView)

        registerButton.setOnClickListener {
            postUser()
        }

        dateOfBirth.setOnClickListener{
            datePickerDialogue()
        }

        logInText.setOnClickListener {
            moveToLogin()
        }

    }

    private fun postUser() {
            if (userName.text.toString() == "" && firstName.text.toString() == "" && lastName.text.toString() == ""
                && email.text.toString() == "" && dateOfBirth.text.toString() == "" && userMobile.text.toString() == "" && gender.text.toString() == ""
                && password.text.toString() == "") {
                userName.error = "User name required"
                firstName.error = "First Name required"
                lastName.error = "Last name Required"
                email.error = "Email Required"
                dateOfBirth.error = "DOB Required"
                userMobile.error = "Contact Required"
                gender.error = "Gender Required"
                password.error = "Password Required"
            } else if (userName.text.toString() == "") {
                userName.error = "User name required"

            }else if (firstName.text.toString() == "") {
                firstName.error = "First Name required"

            }else if (lastName.text.toString() == "") {
                lastName.error = "Last name Required"

            } else if (email.text.toString() == "") {
                email.error = "Email Required"

            } else if (dateOfBirth.text.toString() == "") {
                dateOfBirth.error = "DOB Required"

            } else if (userMobile.text.toString() == "") {
                userMobile.error = "Contact Required"

            } else if (gender.text.toString() == "") {
                gender.error = "Gender Required"

            } else if (password.text.toString() == "") {
                password.error = "Password Required"

            } else {
                request.setOnResponseListener { response ->
                    if (response.code == HttpResponse.HTTP_CREATED) {
                        print("Register: ${response.toJSONObject()}")
                        Toast.makeText(applicationContext, "response.code ${response.code}", Toast.LENGTH_LONG).show()

                        userName.setText("")
                        firstName.setText("")
                        lastName.setText("")
                        email.setText("")
                        dateOfBirth.text = ""
                        userMobile.setText("")
                        gender.setText("")
                        password.setText("")

                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                request.setOnErrorListener {
                    println("Not registered: $it")
                }

                val json = JSONObject()
                try {
                    json.put("username", userName.text.toString())
                    json.put("first_name", firstName.text.toString())
                    json.put("last_name", lastName.text.toString())
                    json.put("email", email.text.toString())
                    json.put("date_of_birth", dateOfBirth.text.toString())
                    json.put("gender", gender.text.toString())
                    json.put("mobile", userMobile.text.toString())
                    json.put("password", password.text.toString())
                } catch (e: JSONException) {
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
                }
                request.post(URL_BASE, json)
            }
    }

    @SuppressLint("SimpleDateFormat")
    private fun datePickerDialogue() {
        val dateCalendar = Calendar.getInstance()
        val year = dateCalendar.get(Calendar.YEAR)
        val month = dateCalendar.get(Calendar.MONTH)
        val day = dateCalendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this,
        DatePickerDialog.OnDateSetListener {
                _, selectedYear, selectedMonth, selectedDayofMonth ->
            val selectedDate = "$selectedYear-${selectedMonth+1}-$selectedDayofMonth"
            dateOfBirth.text = selectedDate
        }
        , year, month, day).show()
    }

    private fun moveToLogin() {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}