package com.dicoding.asclepius.view.result

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ViewModelFactory
import com.dicoding.asclepius.view.MainViewModel

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private val resultViewModel: ResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = intent.getStringExtra("IMAGE_URI")
        val classificationResults = intent.getStringArrayListExtra("CLASSIFICATION_RESULTS")

        // Pass data to ViewModel
        resultViewModel.setImageUri(imageUri)
        resultViewModel.setClassificationResults(classificationResults)

        // Observe ViewModel data and update UI
        resultViewModel.imageUri.observe(this, Observer { uri ->
            binding.resultImage.setImageURI(uri)
        })

        resultViewModel.classificationResults.observe(this, Observer { results ->
            binding.resultText.text = results.joinToString()
        })
    }


}