package com.dicoding.asclepius.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.asclepius.data.entity.PredictionHistory

@Database(entities = [PredictionHistory::class], version = 1, exportSchema = false)
abstract class PredictionHistoryDatabase : RoomDatabase() {
    abstract fun predictionHistoryDao(): PredictionHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: PredictionHistoryDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): PredictionHistoryDatabase {
            if (INSTANCE == null) {
                synchronized(PredictionHistoryDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        PredictionHistoryDatabase::class.java, "predictionHistory"
                    )
                        .build()
                }
            }
            return INSTANCE as PredictionHistoryDatabase
        }
    }
}