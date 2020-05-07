package com.nvnrdhn.happyface

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.lifecycle.LifecycleOwner
import androidx.preference.PreferenceManager
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

private const val TAG = "HappyFace"
private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
private const val PHOTO_EXTENSION = ".jpg"
private const val RATIO_4_3_VALUE = 4.0 / 3.0
private const val RATIO_16_9_VALUE = 16.0 / 9.0

const val RESULT_KEY = "FACE_DATA"

val apiService by lazy {
    ApiService.create()
}

class MainActivity : AppCompatActivity() {
    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private lateinit var imageCapture: ImageCapture
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        PreferenceManager.getDefaultSharedPreferences(this).apply {
            if (!getBoolean(OnboardFragment.COMPLETED_ONBOARDING_PREF, false)) {
                startActivity(Intent(this@MainActivity, OnboardingActivity::class.java))
                finish()
            }
        }
        cameraExecutor = Executors.newSingleThreadExecutor()
        outputDirectory = getOutputDirectory(this)
        if (allPermissionsGranted()) {
            preview_view.post { startCamera() }
        }
        else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                preview_view.post { startCamera() }
            }
            else {
                Toast.makeText(this, "Permissions not granted.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindPreview(cameraProvider : ProcessCameraProvider) {
        val preview = Preview.Builder()
            .build()

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetRotation(preview_view.display.rotation)
            .setTargetResolution(Size(480, 640))
            .build()

        val camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview, imageCapture)
        preview.setSurfaceProvider(preview_view.createSurfaceProvider(camera.cameraInfo))
        capture.setOnClickListener { takePhoto() }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
    }

    private fun takePhoto() {
        capture.visibility = View.GONE
        tv_1.visibility = View.GONE
        loading.visibility = View.VISIBLE
        val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(outputFileOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//                val savedUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
//                // If the folder selected is an external media directory, this is
//                // unnecessary but otherwise other apps will not be able to access our
//                // images unless we scan them using [MediaScannerConnection]
//                val mimeType = MimeTypeMap.getSingleton()
//                    .getMimeTypeFromExtension(savedUri.toFile().extension)
//                MediaScannerConnection.scanFile(
//                    this@MainActivity,
//                    arrayOf(savedUri.toString()),
//                    arrayOf(mimeType)
//                ) { _, uri ->
//                    Log.d(TAG, "Image capture scanned into media store: $uri")
//                }
                detectPhoto(photoFile)
            }

            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
                Toast.makeText(this@MainActivity, exception.localizedMessage, Toast.LENGTH_LONG).show()
                runOnUiThread {
                    capture.visibility = View.VISIBLE
                    tv_1.visibility = View.VISIBLE
                    loading.visibility = View.GONE
                }
            }
        })
    }

    private fun detectPhoto(photo: File) {
        runOnUiThread { tv_2.visibility = View.VISIBLE }
        apiService.detect(RequestBody.create(MediaType.parse("application/octet-stream"), photo))
            .enqueue(object : Callback<List<Model.FaceData>> {
                override fun onFailure(call: Call<List<Model.FaceData>>, t: Throwable) {
                    photo.delete()
                    t.printStackTrace()
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, t.localizedMessage, Toast.LENGTH_SHORT).show()
                        capture.visibility = View.VISIBLE
                        tv_1.visibility = View.VISIBLE
                        loading.visibility = View.GONE
                        tv_2.visibility = View.GONE
                    }
                }

                override fun onResponse(
                    call: Call<List<Model.FaceData>>,
                    response: Response<List<Model.FaceData>>
                ) {
                    photo.delete()
                    if (response.isSuccessful and !response.body().isNullOrEmpty()) {
                        val body = response.body()
                        val result = Intent(this@MainActivity, ResultActivity::class.java)
                        val bundle = Bundle()
                        bundle.putSerializable(RESULT_KEY, body?.firstOrNull())
                        startActivity(result.putExtras(bundle))
                    }
                    else {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Error: No face detected.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    runOnUiThread {
                        capture.visibility = View.VISIBLE
                        tv_1.visibility = View.VISIBLE
                        loading.visibility = View.GONE
                        tv_2.visibility = View.GONE
                    }
                }
            })
    }

    /** Use external media if it is available, our app's file directory otherwise */
    private fun getOutputDirectory(context: Context): File {
        val appContext = context.applicationContext
        val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
            File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else appContext.filesDir
    }

    /** Helper function used to create a timestamped file */
    private fun createFile(baseFolder: File, format: String, extension: String) =
        File(baseFolder, SimpleDateFormat(format, Locale.US)
            .format(System.currentTimeMillis()) + extension)
}
