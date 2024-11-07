package com.dicoding.asclepius.view.result

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.dicoding.asclepius.data.remote.retrofit.ApiConfig
import com.dicoding.asclepius.data.repository.NewsRepository
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.view.result.ResultViewModel
import com.dicoding.asclepius.view.result.ViewModelFactory

class ResultActivity : AppCompatActivity() {
    private val apiService = ApiConfig.getApiService()
    private lateinit var binding: ActivityResultBinding
    private val resultViewModel by viewModels<ResultViewModel>{
        ViewModelFactory.getInstance(NewsRepository.getInstance(apiService))
    }

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

        //Get the news and show it

    }


}