package com.example.newsapp.data.repository

import android.util.Log
import com.example.newsapp.data.baseRepository.BaseDataSource
import com.example.newsapp.data.local.NewsDao
import com.example.newsapp.data.remote.NewsApiService
import com.example.newsapp.domain.model.NewsResponse
import com.example.newsapp.domain.repository.NewsRepository
import com.example.newsapp.util.Result
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsApiService: NewsApiService,
    private val newsDao: NewsDao
) : BaseDataSource(), NewsRepository{

    override suspend fun getNews(country: String): Result<NewsResponse> {
        val result = getResult {
            newsApiService.getNews()
        }

        if (result.status == Result.Status.SUCCESS && result.data != null){

            val news = result.data.articles

            try {
                newsDao.insertALl(news.map { it })
            }catch (e: Exception){
                Log.e("NewsRepImpl", "Error inserting news into database: ${e.message}")
            }

        }
        return result
    }
}