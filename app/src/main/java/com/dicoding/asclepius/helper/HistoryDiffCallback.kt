package com.dicoding.asclepius.helper

import androidx.recyclerview.widget.DiffUtil
import com.dicoding.asclepius.data.entity.PredictionHistory

class HistoryDiffCallback(private val oldPredictionHistoryList: List<PredictionHistory>, private val newPredictionHistoryList: List<PredictionHistory>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldPredictionHistoryList.size
    override fun getNewListSize(): Int = newPredictionHistoryList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldPredictionHistoryList[oldItemPosition].id == newPredictionHistoryList[newItemPosition].id
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldPredictionHistory = oldPredictionHistoryList[oldItemPosition]
        val newPredictionHistory = newPredictionHistoryList[newItemPosition]
        return oldPredictionHistory.imageUri == newPredictionHistory.imageUri &&
                oldPredictionHistory.predictionResult == newPredictionHistory.predictionResult &&
                oldPredictionHistory.confidenceScore == newPredictionHistory.confidenceScore &&
                oldPredictionHistory.date == newPredictionHistory.date
    }
}