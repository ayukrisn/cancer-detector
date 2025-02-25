package com.dicoding.asclepius.view.result

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.data.remote.retrofit.ApiConfig
import com.dicoding.asclepius.data.repository.NewsRepository
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.data.Result
import com.dicoding.asclepius.view.result.ResultViewModel
import com.dicoding.asclepius.view.result.ViewModelFactory

class ResultActivity : AppCompatActivity() {
    private val apiService = ApiConfig.getApiService()
    private lateinit var binding: ActivityResultBinding
    private lateinit var resultAdapter: ResultAdapter
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
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        resultAdapter = ResultAdapter()

        binding.rvNews.apply {
            layoutManager = LinearLayoutManager(this@ResultActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = resultAdapter
        }

        resultViewModel.news.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    val news = result.data

                    val filteredNews = news.filter { it.title != "[Removed]" }

                    Log.d("ResultActivity", "Passing ${filteredNews.size} filtered items to adapter")
                    filteredNews.forEach { Log.d("ResultActivity", "Article: ${it.title}") }
                    resultAdapter.submitList(filteredNews)
                }
                is Result.Error -> {
                    showErrorDialog(result.error)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }


}