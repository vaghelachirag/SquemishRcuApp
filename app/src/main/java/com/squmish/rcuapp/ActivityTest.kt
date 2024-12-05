package com.squmish.rcuapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.squmish.rcuapp.databinding.ActivityTestBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


class ActivityTest: AppCompatActivity()  {
    private lateinit var binding: ActivityTestBinding
    private var WebView: WebView? = null
    private val TAG: String = MainActivity::class.java.simpleName


    private var mCM: String? = null
    private var mUM: ValueCallback<Uri>? = null
    private var mUMA: ValueCallback<Array<Uri>>? = null
    private
    val FCR: Int = 1

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WebView = binding.webView1

        if (Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this@ActivityTest,
                arrayOf<String>(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ),
                1
            )
        }

        val webSettings = WebView!!.settings
        webSettings.javaScriptEnabled = true
        webSettings.allowFileAccess = true

        webSettings.mixedContentMode = 0
        WebView!!.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        WebView!!.webViewClient = Callback()

        //WebView.loadUrl("https://infeeds.com/");
        WebView!!.loadUrl("https://poonammanagement.com/MobileApp/ReimubersementIndex?UserId=74&BranchId=1&Latitude=0.0&Longitude=0.0&Token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJKV1RTZXJ2aWNlQWNjZXNzVG9rZW4iLCJqdGkiOiJmOWRlNTE5Yy1hY2E4LTQ3YmYtYjhlYi0wM2JlYTI0M2FiNjAiLCJpYXQiOiIwMy0xMi0yMDI0IDE2OjMxOjM3IiwiVXNlckxvZ0lkIjoiMjg2NDg1IiwiVXNlcklkIjoiNzQiLCJFbXBsb3llZUlkIjoiMTM0IiwiQnVzaW5lc3NJZCI6IjIwIiwiZXhwIjoxNzMzMjcwNDAwLCJpc3MiOiJKV1RBdXRoZW50aWNhdGlvblNlcnZlciIsImF1ZCI6IkpXVFNlcnZpY2VQb3N0bWFuQ2xpZW50In0.D0uZIChupig4KRy9BZiDcDPGNeS6pwJuaKRTL3gKt9Y")
        WebView!!.webChromeClient = object : WebChromeClient() {
            //For Android 3.0+
            fun openFileChooser(uploadMsg: ValueCallback<Uri>) {
                mUM = uploadMsg
                val i = Intent(Intent.ACTION_GET_CONTENT)
                i.addCategory(Intent.CATEGORY_OPENABLE)
                i.setType("*/*")
                this@ActivityTest.startActivityForResult(
                    Intent.createChooser(i, "File Chooser"),
                   FCR
                )
            }

            //For Android 4.1+
            fun openFileChooser(
                uploadMsg: ValueCallback<Uri>,
                acceptType: String?,
                capture: String?
            ) {
                mUM = uploadMsg
                val i = Intent(Intent.ACTION_GET_CONTENT)
                i.addCategory(Intent.CATEGORY_OPENABLE)
                i.setType("*/*")
                this@ActivityTest.startActivityForResult(
                    Intent.createChooser(i, "File Chooser"),
                    FCR
                )
            }

            //For Android 5.0+
            @SuppressLint("QueryPermissionsNeeded")
            override fun onShowFileChooser(
                webView: WebView, filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                mUMA?.onReceiveValue(null)
                mUMA = filePathCallback
                var takePictureIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent!!.resolveActivity(this@ActivityTest.packageManager) != null) {
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                        takePictureIntent.putExtra("PhotoPath", mCM)
                    } catch (ex: IOException) {
                       // Log.e(MainActivity.TAG, "Image file creation failed", ex)
                    }
                    if (photoFile != null) {
                        mCM = "file:" + photoFile.absolutePath
                        takePictureIntent.putExtra(
                            MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile)
                        )
                    } else {
                        takePictureIntent = null
                    }
                }
                val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
                contentSelectionIntent.setType("*/*")
                val intentArray = if (takePictureIntent != null) {
                    arrayOf<Intent?>(takePictureIntent)
                } else {
                    arrayOfNulls(0)
                }

                val chooserIntent = Intent(Intent.ACTION_CHOOSER)
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser")
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
                startActivityForResult(chooserIntent, FCR)
                return true
            }
        }


        // Create an image file
        @Throws(IOException::class)
        fun createImageFiles(): File {
            @SuppressLint("SimpleDateFormat") val timeStamp =
                SimpleDateFormat("yyyyMMdd_HHmmss").format(
                    Date()
                )
            val imageFileName = "img_" + timeStamp + "_"
            val storageDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            return File.createTempFile(imageFileName, ".jpg", storageDir)
        }

    }

    class Callback : WebViewClient() {
        @Deprecated("Deprecated in Java")
        override fun onReceivedError(
            view: WebView,
            errorCode: Int,
            description: String,
            failingUrl: String
        ) {

        }
    }

    // Create an image file
    @Throws(IOException::class)
    private fun createImageFile(): File {
        @SuppressLint("SimpleDateFormat") val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss").format(
                Date()
            )
        val imageFileName = "img_" + timeStamp + "_"
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    if (WebView!!.canGoBack()) {
                        WebView!!.goBack()
                    } else {
                        finish()
                    }
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig!!)
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        var results: Array<Uri>? = null
        //Check if response is positive
        if (resultCode == RESULT_OK) {
            if (requestCode == FCR) {
                if (null == mUMA) {
                    return
                }
                if (intent == null) {
                    //Capture Photo if no image available
                    if (mCM != null) {
                        results = arrayOf(Uri.parse(mCM))
                    }
                } else {
                    val dataString = intent.dataString
                    if (dataString != null) {
                        results = arrayOf(Uri.parse(dataString))
                    }
                }
            }
        }
        mUMA!!.onReceiveValue(results)
        mUMA = null
    }

}