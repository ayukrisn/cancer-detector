package com.dicoding.asclepius.data.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(tableName = "predictionHistory")
@Parcelize
class PredictionHistory(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "imageUri")
    val imageUri: String,

    @ColumnInfo(name = "predictionResult")
    val predictionResult: String,

    @ColumnInfo(name = "confidenceScore")
    val confidenceScore: Float,

    @ColumnInfo(name = "date")
    val date: String
) : Parcelable