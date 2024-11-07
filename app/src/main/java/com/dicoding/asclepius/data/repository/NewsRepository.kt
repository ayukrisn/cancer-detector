package com.dicoding.asclepius.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.dicoding.asclepius.data.Result
import com.dicoding.asclepius.data.remote.response.ArticlesItem
import com.dicoding.asclepius.data.remote.response.NewsResponse
import com.dicoding.asclepius.data.remote.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsRepository private constructor(
    private val apiService: ApiService,
) {
    companion object {
        @Volatile
        private var instance: NewsRepository? = null

        fun getInstance(apiService: ApiService): NewsRepository =
            instance ?: synchronized(this) {
                instance ?: NewsRepository(apiService)
            }.also { instance = it }
    }

    /***
     * Get the news data from API
     */
    private val result = MediatorLiveData<Result<List<ArticlesItem>>>()
    private val query = "cancer"
    private val category = "health"
    private val language = "en"
    private val apiKey = "9c036f7a01e44e65b7528f83ebf9d2dd"

    fun getNews(): LiveData<Result<List<ArticlesItem>>> {
        result.value = Result.Loading
        val client = apiService.getTopHeadlines(query, category, language, apiKey)

        client.enqueue(object: Callback<NewsResponse> {
            override fun onResponse(
                call: Call<NewsResponse>,
                response: Response<NewsResponse>
            ) {
                if (response.isSuccessful) {
                    val news = response.body()?.articles
                    if (news != null) {
                        val nonNullNews = news.filterNotNull()
                        result.value = Result.Success(nonNullNews)
                    } else {
                        result.value = Result.Error("Failed to fetch news: ${response.message()}")
                    }

                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                result.value = Result.Error("Failed to fetch news: ${t.message}")
            }
        })
        return result
    }

}