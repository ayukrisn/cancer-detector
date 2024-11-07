package com.dicoding.asclepius.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.asclepius.data.local.entity.PredictionHistory

@Dao
interface PredictionHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(predictionHistory: PredictionHistory)

    @Query("SELECT * FROM predictionHistory")
    fun getPredictionHistory(): LiveData<List<PredictionHistory>>

    @Query("SELECT * FROM predictionHistory WHERE id = :id")
    fun getPredictionHistoryById(id: Int):  LiveData<PredictionHistory>
}