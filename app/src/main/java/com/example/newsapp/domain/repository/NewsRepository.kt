package com.example.newsapp.domain.repository

import com.example.newsapp.domain.model.NewsResponse
import com.example.newsapp.util.Result

interface NewsRepository {
    suspend fun getNews(country: String) : Result<NewsResponse>
}