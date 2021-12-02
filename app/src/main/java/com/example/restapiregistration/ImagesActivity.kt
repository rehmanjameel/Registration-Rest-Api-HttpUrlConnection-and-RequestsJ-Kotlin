package com.example.restapiregistration

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.OutputStreamWriter
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider.getUriForFile
import java.net.URI
import java.security.AccessController.getContext
import android.content.ClipData
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.MediaPlayer
import android.os.Message
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.core.content.FileProvider
import pk.codebase.requests.*

class ImagesActivity : AppCompatActivity() {

    lateinit var uploadImageButton: Button
    lateinit var pickImageButton: Button

    //private val context: Context? = null
    lateinit var sharedPreferences: SharedPreferences
    lateinit var imageView: ImageView
    lateinit var imageUri: Uri

    companion object {
        private val PERMISSION_CODE = 1001
    }

    val imageUrl = URL("http://192.168.100.242:8000/api/image/")

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images)

        title = "Select Image"

        imageView = findViewById(R.id.galleryImage)

        sharedPreferences = getSharedPreferences("getToken", Context.MODE_PRIVATE)

        uploadImageButton = findViewById(R.id.imageUpload)
        pickImageButton = findViewById(R.id.pickImage)

        pickImageButton.setOnClickListener {

            pickImage()
        }

        uploadImageButton.setOnClickListener {
            uploadImage()
        }

    }

    private fun pickImage() {
        //Check Version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Check permission is granted or not to pick the image from external storage
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permission, PERMISSION_CODE)
            } else {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                galleryActivityResultLauncher.launch(intent)
            }
        } else {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            galleryActivityResultLauncher.launch(intent)
        }
    }

    private var galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                //image picked
                    showToast("Image Picked From Gallery")
                //get image uri
                val intent = result.data
                if (intent != null) {
                    imageUri = intent.data!!
                }
                Log.e("Imageuri", imageUri.toString())
                imageView.setImageURI(imageUri)
            } else {
                showToast("Cancelled")
            }
        }
    )

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun uploadImage(){
//        val bitmap: Bitmap
//        //get bitmap from uri
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            val source = ImageDecoder.createSource(contentResolver, imageUri!!)
//            Log.e("Image source", source.toString())
//
//            bitmap = ImageDecoder.decodeBitmap(source)
//            Log.e("Image Decoder", bitmap.toString())
//            Log.e("Image_Decoder", imageUri.toString())
//        } else {
//            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
//            Log.e("ImageDecoder", bitmap.toString())
//        }
//
//        Log.e("Paths:", imagePath)

        val realPathUtil = RealPathUtil()
        val real = realPathUtil.getRealPath(applicationContext, imageUri)
        Log.e("Real", real)
        val file = File(real)
        Log.e("files", "$file")
        val token = sharedPreferences.getString("Token", "")
        Log.e("token", "$token")

//        var contentUri: Uri? = null

        val request = HttpRequest()
        request.setOnResponseListener { response ->
            if (response.code == HttpResponse.HTTP_OK) {
                    Log.e("response", "${response.code}")
                    Log.e("response", real)
                    showToast("Image Uploaded")

                val intent = Intent(applicationContext, HomeScreen::class.java)
                startActivity(intent)
                finish()
                }
        }
        request.setOnErrorListener {
                // There was an error, deal with it
                Log.e("Httperror", "Error")
            showToast("Image not uploaded")
        }
        val headers = HttpHeaders("Authorization", "Token $token")
        val data = FormData()
        data.put("image", File("$file"))
        Log.e("test", "$file")
        LoadingScreen.displayLoadingWithText(applicationContext, "Please wait...", false)
        request.post("$imageUrl", data, headers)


//        try {
//            GlobalScope.launch(Dispatchers.IO) {
//
//                val boundary = "BoundaddFilePartary-${System.currentTimeMillis()}"
//
//                val imagePath = File(imageUri, "")
////                val newFile = File(imagePath, ".")
////                contentUri = FileProvider.getUriForFile(applicationContext, "com.example.restapiregistration.fileprovider", imagePath)
//
//                val intent = Intent()
//                intent.clipData = ClipData.newRawUri("", imageUri)
//                intent.addFlags(
//                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//                )
//
////                val file = File(imagePath)
//                val fileInputStream = FileInputStream(imagePath)
//
//                val httpURLConnection = imageUrl.openConnection() as HttpURLConnection
//                httpURLConnection.addRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
//                httpURLConnection.setRequestProperty("Authorization", "Token $token")
//                httpURLConnection.requestMethod = "POST"
//                httpURLConnection.doInput = true
//                httpURLConnection.doOutput = true
//
//                val outputStreamToRequestBody = httpURLConnection.outputStream
//                val httpRequestBodyWriter = BufferedWriter(OutputStreamWriter(outputStreamToRequestBody))
//                bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStreamToRequestBody)
//
//
//                // Add the email in the post data
//                httpRequestBodyWriter.write("\n\n--$boundary\n")
//                httpRequestBodyWriter.write("Content-Disposition: form-data; name=\"email\"")
//                httpRequestBodyWriter.write("\n\n")
//                httpRequestBodyWriter.write("abdulrehmancs17@gmail.com")
//
//                // Add the part to describe the file
//                httpRequestBodyWriter.write("\n--$boundary\n")
//                httpRequestBodyWriter.write(
//                    "Content-Disposition: form-data;"
//                            + "name=\"image\";"
//                            + "filename=\"" + newFile.name + "\""
//                            + "\nContent-Type: picture.jpg\r\n"
//                )
//                httpRequestBodyWriter.flush()
//
//                // Write the file
//                //val inputStreamToFile = FileInputStream(file)
//                var bytesRead: Int
//                val dataBuffer = ByteArray(1024 * 1024)
//                while (fileInputStream.read(dataBuffer).also { bytesRead = it } != -1) {
//                    outputStreamToRequestBody.write(dataBuffer, 0, bytesRead)
//                }
//                outputStreamToRequestBody.flush()
//
//                // End of the multipart request
//                httpRequestBodyWriter.write("\n--$boundary--\n")
//                httpRequestBodyWriter.flush()
//
//                // Close the streams
//                outputStreamToRequestBody.close()
//                httpRequestBodyWriter.close()
//
//                // Check if the connection is successful
//                val responseCode = httpURLConnection.responseCode
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    val response = httpURLConnection.inputStream.bufferedReader()
//                        .use { it.readText() }  // defaults to UTF-8
//                    withContext(Dispatchers.Main) {
//
//                        // Convert raw JSON to pretty JSON using GSON library
//                        val gson = GsonBuilder().setPrettyPrinting().create()
//                        val prettyJson = gson.toJson(JsonParser.parseString(response))
//                        Log.d("Pretty Printed JSON :", prettyJson)
//
//                        // Open DetailsActivity with the results
//                        /*val intent = Intent(this@MainActivity, DetailsActivity::class.java)
//                        intent.putExtra("json_results", prettyJson)
//                        this@MainActivity.startActivity(intent)*/
//                    }
//                } else {
//                    Log.e("HTTPURLCONNECTION_ERROR", responseCode.toString())
//                }
//            }
//        } catch (e: Exception) {
//
//        }
    }
}