package com.example.restapiregistration

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.TextureView
import android.view.Window
import android.widget.TextView
import java.lang.Exception

object LoadingScreen {

    var dialog: Dialog? = null

    fun displayLoadingWithText(context: Context?, text: String?, cancellable: Boolean) {
        dialog = Dialog(context!!)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.progress_layout)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setCancelable(cancellable)
        val progressText: TextView = dialog!!.findViewById(R.id.progressTextView)
        progressText.text = text

        try {
            dialog!!.show()
        } catch (e: Exception) {
            Log.e("Dialog Exception", e.toString())
        }
    }
}