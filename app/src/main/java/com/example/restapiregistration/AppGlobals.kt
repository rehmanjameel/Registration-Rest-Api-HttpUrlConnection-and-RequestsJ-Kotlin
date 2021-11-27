package com.example.restapiregistration

import android.app.Application
import android.content.Context
import android.content.SharedPreferences


class AppGlobals: Application() {

    lateinit var context: Context

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        println("here it is")
    }

    fun getPreferenceManager(): SharedPreferences {
        return context.getSharedPreferences("shared_prefs", MODE_PRIVATE)
    }

    /*fun logout() {
        val sharedPreferences = getPreferenceManager()
        sharedPreferences.edit().clear().apply()
    }

    fun saveLogin(value: Boolean) {
        val sharedPreferences = getPreferenceManager()
        sharedPreferences.edit().putBoolean(KEY_LOGGED_IN, value).apply()
    }

    fun isLoggedIn(): Boolean {
        val sharedPreferences = getPreferenceManager()
        return sharedPreferences.getBoolean(KEY_LOGGED_IN, false)
    }*/

    fun saveDataToSharedPreferences(key: String?, value: String?) {
        val sharedPreferences = getPreferenceManager()
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getStringFromSharedPreferences(key: String?): String? {
        val sharedPreferences = getPreferenceManager()
        return sharedPreferences.getString(key, "")
    }
}