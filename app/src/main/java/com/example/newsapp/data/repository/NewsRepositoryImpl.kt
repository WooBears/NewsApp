package com.example.newsapp.data.repository

import com.example.newsapp.data.baseRepository.BaseDataSource
import com.example.newsapp.data.remote.NewsApiService
import com.example.newsapp.domain.model.NewsResponse
import com.example.newsapp.domain.repository.NewsRepository
import com.example.newsapp.util.Result
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsApiService: NewsApiService
) : BaseDataSource(), NewsRepository{

    override suspend fun getNews(country: String): Result<NewsResponse> {
        val result = getResult {
            newsApiService.getNews()
        }
        return result
    }
}