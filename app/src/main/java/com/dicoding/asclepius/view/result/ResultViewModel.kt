package com.dicoding.asclepius.view.result

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.entity.PredictionHistory
import com.dicoding.asclepius.data.repository.PredictionHistoryRepository

class ResultViewModel : ViewModel() {
    private val _imageUri = MutableLiveData<Uri>()
    val imageUri: LiveData<Uri> get() = _imageUri

    private val _classificationResults = MutableLiveData<List<String>>()
    val classificationResults: LiveData<List<String>> get() = _classificationResults

    fun setImageUri(uriString: String?) {
        uriString?.let {
            _imageUri.value = Uri.parse(it)
        }
    }

    fun setClassificationResults(results: ArrayList<String>?) {
        _classificationResults.value = results ?: emptyList()
    }
}