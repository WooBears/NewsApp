package com.example.newsapp.data.repository

import android.util.Log
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
) : BaseDataSource(), NewsRepository{

    override suspend fun getNews(country: String): Result<NewsResponse> {

        val result = getResult {
            newsApiService.getNews()
        }

        // Store data locally
        if (result.status == Result.Status.SUCCESS && result.data != null){

            val news = result.data.articles

            try {
                newsDao.insertALl(news.map { it })
            }catch (e: Exception){
                //TODO
            }

        } else {
            //TODO
        }
        return result
    }

    override suspend fun getAllCachedNews(): Result<List<Article>> {
        return newsDao.getAll().let {
            Result.success(it)
        }
    }

    override suspend fun addToFavorites(articleId: Int) {
        newsDao.addToFavourites(articleId)
        //newsDao.getAllFavorites()
    }

    override suspend fun removeFavorites(articleId: Int) {
        newsDao.removeFavourites(articleId)
    }

    override suspend fun getAllFavorites(): List<Article> {
        val favorites = newsDao.getAllFavorites()
        return favorites
    }

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
            Log.e("NetworkError", "Unable to resolve host: ${e.message}")
            Result.error("Network unavailable. Please check your connection.")
        } catch (e: Exception) {
            Log.e("NetworkError", "Error fetching news: ${e.message}")
            Result.error(e.message ?: "Unknown error")
        }
    }
}