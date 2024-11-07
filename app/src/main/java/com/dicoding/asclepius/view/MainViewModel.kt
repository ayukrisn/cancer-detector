package com.dicoding.asclepius.view

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.local.entity.PredictionHistory
import com.dicoding.asclepius.data.repository.PredictionHistoryRepository
import com.dicoding.asclepius.helper.DateHelper

class MainViewModel(application: Application) : ViewModel() {
    private val repository = PredictionHistoryRepository(application)

    private val _imageUri = MutableLiveData<Uri?>()
    val imageUri: LiveData<Uri?> get() = _imageUri

    private val _classificationResults = MutableLiveData<String>()
    val classificationResults: LiveData<String> get() = _classificationResults

    private val _confidenceScore = MutableLiveData<Float>()

    fun setResultData(uriString: String?, results: String?, confidenceScore: Float?) {
        uriString?.let {
            _imageUri.value = Uri.parse(it)
        }
        _classificationResults.value = results
        _confidenceScore.value = confidenceScore

        Log.d("ResultViewModel", "Attempting to insert prediction history...")
        insertPredictionHistory()
    }

    private fun insertPredictionHistory() {
        val uri = _imageUri.value
        val results = _classificationResults.value
        val confidenceScore: Float = _confidenceScore.value ?: 0.0f

        if (uri != null && results != null) {
            Log.d("ResultViewModel", "Inserting prediction history with URI: $uri and results: $results")
            val predictionHistory = PredictionHistory(
                imageUri = uri.toString(),
                predictionResult = results,
                confidenceScore = confidenceScore,
                date = DateHelper.getCurrentDate()
            )
            repository.insert(predictionHistory)
        }
    }

    fun setImageUri(uri: Uri?) {
        uri?.let {
            _imageUri.value = uri
        }
    }
}
