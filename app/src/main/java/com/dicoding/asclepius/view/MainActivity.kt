package com.dicoding.asclepius.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.view.history.ViewModelFactory
import com.dicoding.asclepius.view.history.HistoryActivity
import com.dicoding.asclepius.view.result.ResultActivity
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.support.label.Category
import java.io.File
import java.util.UUID

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    private val mainViewModel by viewModels<MainViewModel>{
        ViewModelFactory.getInstance(application)
    }

    /***
     * Asking for permission request
     */
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Permission request granted")
            } else {
                showToast("Permission request denied")
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    /***
     * On Create Main Activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        // Observe the current image URI from ViewModel
        mainViewModel.imageUri.observe(this) { uri ->
            uri?.let {
                binding.previewImageView.setImageURI(it)
            }
        }


        binding.galleryButton.setOnClickListener{ startGallery() }
        binding.analyzeButton.setOnClickListener{ analyzeImage() }
        binding.historyButton.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    /***
     * Accessing Gallery
     */
    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            mainViewModel.setImageUri(uri)
            startCrop(uri) //Crop the image
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    /***
     * Function to crop the image
     */
    private fun startCrop(uri: Uri) {
        val uniqueFileName = "cropped_image_${UUID.randomUUID()}.jpg"
        val destinationUri = Uri.fromFile(File(cacheDir, uniqueFileName))
        UCrop.of(uri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(500, 500)
            .start(this)
    }

    // Handle result from uCrop
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            mainViewModel.setImageUri(UCrop.getOutput(data!!)!!)
            showImage() // Display the cropped image
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            showToast("Crop error: ${cropError?.message}")
        }
    }

    /***
     * Show image chosen
     */
    private fun showImage() {
        mainViewModel.imageUri.value?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    /***
     * Start analyzing image using ImageClassifierHelper
     */
    private fun analyzeImage() {
        mainViewModel.imageUri.value?.let { uri ->
            imageClassifierHelper = ImageClassifierHelper(
                context = this,
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        showToast(error)
                    }

                    override fun onResults(result: Category?, inferenceTime: Long) {
                        result?.let {
                            moveToResult(it)
                        } ?: showToast("No classification results.")
                    }
                }
            )
            imageClassifierHelper.classifyStaticImage(this, uri)
        } ?: showToast("No image selected.")
    }


    /***
     * Move to result activity
     */
    private fun moveToResult(result: Category) {
        val intent = Intent(this, ResultActivity::class.java)
        val classificationResult = "${result.label} : ${result.score * 100}%"
        intent.putStringArrayListExtra("CLASSIFICATION_RESULTS", arrayListOf(classificationResult))
        intent.putExtra("IMAGE_URI", mainViewModel.imageUri.value.toString())
        mainViewModel.setResultData(mainViewModel.imageUri.value.toString(), result.label, result.score)

        startActivity(intent)
    }

    /***
     * Helpers
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}