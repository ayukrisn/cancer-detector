package com.dicoding.asclepius.view.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.data.entity.PredictionHistory
import com.dicoding.asclepius.databinding.ItemHistoryBinding
import com.dicoding.asclepius.helper.HistoryDiffCallback

class HistoryAdapter: RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    private val listHistory = ArrayList<PredictionHistory>()
    fun setListHistory(listHistory: List<PredictionHistory>) {
        val diffCallback = HistoryDiffCallback(this.listHistory, listHistory)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.listHistory.clear()
        this.listHistory.addAll(listHistory)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(listHistory[position])
    }

    override fun getItemCount(): Int {
        return listHistory.size
    }

    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(predictionHistory: PredictionHistory) {
            val predictionResult = predictionHistory.predictionResult + " | Akurasi: " + String.format("%.2f", predictionHistory.confidenceScore)
            val predictionDate = "Tanggal prediksi:" + predictionHistory.date
            with(binding) {
                Glide.with(itemView.context)
                    .load(predictionHistory.imageUri)
                    .into(binding.imgCancer)
                tvPredictionResult.text = predictionResult
                tvDate.text = predictionDate
            }
        }
    }
}