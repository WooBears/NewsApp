package com.example.newsapp.data.repository

import android.util.Log
import android.widget.Toast
import com.example.newsapp.data.baseRepository.BaseDataSource
import com.example.newsapp.data.local.NewsDao
import com.example.newsapp.data.remote.NewsApiService
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.model.NewsResponse
import com.example.newsapp.domain.repository.NewsRepository
import com.example.newsapp.util.Result
import retrofit2.Response
import java.net.UnknownHostException
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsDao: NewsDao,
    private val newsApiService: NewsApiService,
) : BaseDataSource(), NewsRepository {

    // getting data from api
    override suspend fun getNews(country: String): Result<List<Article>> {

        val result = getResult {
            newsApiService.getNews()
        }

        // Store data locally
        if (result.status == Result.Status.SUCCESS && result.data != null) {

            val news = result.data
            try {
                refreshArticles(news)
            } catch (e: Exception) {
                Result.error<NewsRepositoryImpl>(e.message ?: "Error inserting into room " )
            }
        }
        return result
    }

    // getting data from catch
    override suspend fun getAllCachedNews(): Result<List<Article>> {
        return newsDao.getAll().let {
            Result.success(it)
        }
    }

    // adding data to favorites
    override suspend fun addToFavorites(articleId: Int) {
        newsDao.addToFavourites(articleId)
    }
    // removing data from favorites
    override suspend fun removeFavorites(articleId: Int) {
        newsDao.removeFavourites(articleId)
    }

    // getting all data from favorites
    override suspend fun getAllFavorites(): List<Article> {
        val favorites = newsDao.getAllFavorites()
        return favorites
    }

    // getting by category like: sport, technology....
    override suspend fun getNewsByCategory(category: String): Result<List<Article>> {

        return try {
            val response = newsApiService.getNewsByCategory(category)
            if (response.isSuccessful) {
                val articles = response.body()?.articles ?: emptyList()
                Result.success(articles)
            } else {
                Result.error(response.message())
            }
        } catch (e: UnknownHostException) {
            Result.error("Network unavailable. Please check your connection.")
        } catch (e: Exception) {
            Result.error(e.message ?: "Unknown error")
        }
    }

    // searching for the news by title
    override suspend fun getSearchedResult(search: String): Result<List<Article>> {
        return try {
            val articles = newsDao.getSearchedResult(search)
            Result.success(articles)
        } catch (e: Exception) {
            Result.error(e.message ?: "Error fetching search results")
        }
    }

    // cleaning old and inserting new data
    private suspend fun refreshArticles(articles: List<Article>) {
        newsDao.clearAll()
        newsDao.insertALl(articles)
    }
}