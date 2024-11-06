package com.dicoding.asclepius.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.dicoding.asclepius.data.entity.PredictionHistory
import com.dicoding.asclepius.data.room.PredictionHistoryDao
import com.dicoding.asclepius.data.room.PredictionHistoryDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PredictionHistoryRepository(application: Application) {
    private val mPredictionHistoryDao: PredictionHistoryDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = PredictionHistoryDatabase.getDatabase(application)
        mPredictionHistoryDao = db.predictionHistoryDao()
    }

    fun insert(predictionHistory: PredictionHistory) {
        executorService.execute { mPredictionHistoryDao.insert(predictionHistory) }
    }
    fun getPredictionHistory(): LiveData<List<PredictionHistory>> = mPredictionHistoryDao.getPredictionHistory()

    fun getPredictionHistoryById(id: Int): LiveData<PredictionHistory> = mPredictionHistoryDao.getPredictionHistoryById(id)

}