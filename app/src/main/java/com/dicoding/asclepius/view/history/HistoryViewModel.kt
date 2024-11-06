package com.dicoding.asclepius.view.history

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.entity.PredictionHistory
import com.dicoding.asclepius.data.repository.PredictionHistoryRepository

class HistoryViewModel (application: Application): ViewModel() {
    private val mRepository: PredictionHistoryRepository = PredictionHistoryRepository(application)

    fun getPredictionHistory(): LiveData<List<PredictionHistory>> {
        return mRepository.getPredictionHistory()
    }
}