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
import androidx.core.content.ContextCompat
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null
    private lateinit var imageClassifierHelper: ImageClassifierHelper

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

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.galleryButton.setOnClickListener{ startGallery() }
        binding.analyzeButton.setOnClickListener{ analyzeImage() }
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
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    /***
     * Start analyzing image using ImageClassifierHelper
     */
    private fun analyzeImage() {
        if (currentImageUri != null) {
            imageClassifierHelper = ImageClassifierHelper(
                context = this,
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        showToast(error)
                    }

                    // If success, move to result
                    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                        results?.let {
                            // Pass results to ResultActivity
                            moveToResult(results)
                        }
                    }
                }
            )
            // Start classification
            imageClassifierHelper.classifyStaticImage(this, currentImageUri!!)
        } else {
            showToast("No image selected.")
        }
    }

    /***
     * Move to result activity
     */
    private fun moveToResult(results: List<Classifications>) {
        val intent = Intent(this, ResultActivity::class.java)

        // Prepare the classification results to pass to ResultActivity
        val classifications = results.firstOrNull()?.categories?.map {
            "${it.label} : ${it.score * 100}%"
        } ?: listOf("No results")

        // Put classifications and image URI in intent extras
        intent.putStringArrayListExtra("CLASSIFICATION_RESULTS", ArrayList(classifications))
        intent.putExtra("IMAGE_URI", currentImageUri.toString())

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