package com.example.newsapp.domain.repository

import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.model.NewsResponse
import com.example.newsapp.util.Result

interface NewsRepository {
    suspend fun getNews(country: String): Result<List<Article>>
    suspend fun getAllCachedNews(): Result<List<Article>>
    suspend fun addToFavorites(articleId: Int)
    suspend fun removeFavorites(articleId: Int)
    suspend fun getAllFavorites(): List<Article>
    suspend fun getNewsByCategory(category: String): Result<List<Article>>
    suspend fun getSearchedResult(search: String): Result<List<Article>>
}