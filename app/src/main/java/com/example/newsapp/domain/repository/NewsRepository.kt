package com.example.newsapp.domain.repository

import androidx.paging.PagingData
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.model.NewsResponse
import com.example.newsapp.util.Result
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    suspend fun addToFavorites(articleId: Int)
    suspend fun removeFavorites(articleId: Int)
    suspend fun getAllFavorites(): List<Article>
    suspend fun getSearchedResult(search: String): Result<List<Article>>
    suspend fun getPaginatedNewsByCategory(category: String): Flow<PagingData<Article>>
}