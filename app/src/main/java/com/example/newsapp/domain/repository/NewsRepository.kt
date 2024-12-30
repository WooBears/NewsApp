package com.example.newsapp.domain.repository

import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.model.NewsResponse
import com.example.newsapp.util.Result

interface NewsRepository {
    suspend fun getNews(country: String) : Result<NewsResponse>
    suspend fun getAllCachedNews() : Result<List<Article>>
    suspend fun addToFavorites(articleId: Int)
    suspend fun removeFavorites(articleId: Int)
    suspend fun getAllFavorites() : List<Article>
}