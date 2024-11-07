package com.dicoding.asclepius.view.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.data.repository.NewsRepository

class ViewModelFactory private constructor(private val repository: NewsRepository) :
    ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(ResultViewModel::class.java) -> {
                    ResultViewModel(repository) as T
                }
                else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
            }
        }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(repository: NewsRepository): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(repository)
            }.also { instance = it }
    }
    }