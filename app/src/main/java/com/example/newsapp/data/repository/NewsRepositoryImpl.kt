package com.example.newsapp.data.repository

import android.util.Log
import android.widget.Toast
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.newsapp.data.local.NewsDao
import com.example.newsapp.data.local.NewsDatabase
import com.example.newsapp.data.remote.NewsApiService
import com.example.newsapp.data.remoteMediato.NewsRemoteMediator
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.model.NewsResponse
import com.example.newsapp.domain.repository.NewsRepository
import com.example.newsapp.util.Result
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import java.net.UnknownHostException
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsApiService: NewsApiService,
    private val newsDatabase: NewsDatabase,
) : NewsRepository {

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun getPaginatedNewsByCategory(category: String): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20, // Set your page size
                enablePlaceholders = false // Optional: Set to true if needed
            ),
            remoteMediator = NewsRemoteMediator(
                apiService = newsApiService,
                newsDatabase = newsDatabase,
                category = category
            ),
            pagingSourceFactory = {
                newsDatabase.newsDao().getArticlesByCategory()
            } // Returns a PagingSource for Room
        ).flow
    }


    // adding data to favorites
    override suspend fun addToFavorites(articleId: Int) {
        newsDatabase.newsDao().addToFavourites(articleId)
    }

    // removing data from favorites
    override suspend fun removeFavorites(articleId: Int) {
        newsDatabase.newsDao().removeFavourites(articleId)
    }

    // getting all data from favorites
    override suspend fun getAllFavorites(): List<Article> {
        val favorites = newsDatabase.newsDao().getAllFavorites()
        return favorites
    }

    // searching for the news by title
    override suspend fun getSearchedResult(search: String): Result<List<Article>> {
        return try {
            val articles = newsDatabase.newsDao().getSearchedResult(search)
            Result.success(articles)
        } catch (e: Exception) {
            Result.error(e.message ?: "Error fetching search results")
        }
    }

    // cleaning old and inserting new data
    private suspend fun refreshArticles(articles: List<Article>) {
        newsDatabase.newsDao().clearAll()
        newsDatabase.newsDao().insertALl(articles)
    }
}