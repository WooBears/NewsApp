package com.example.newsapp.data.repository

import android.util.Log
import com.example.newsapp.data.baseRepository.BaseDataSource
import com.example.newsapp.data.local.NewsDao
import com.example.newsapp.data.remote.NewsApiService
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.model.NewsResponse
import com.example.newsapp.domain.repository.NewsRepository
import com.example.newsapp.util.Result
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsDao: NewsDao,
    private val newsApiService: NewsApiService,
) : BaseDataSource(), NewsRepository{

    private val catchMemory = mutableListOf<Article>()

    override suspend fun getNews(country: String): Result<NewsResponse> {

        val result = getResult {
            newsApiService.getNews()
        }

        //Store data locally
        if (result.status == Result.Status.SUCCESS && result.data != null){

            val news = result.data.articles

            try {
                newsDao.insertALl(news.map { it })
                catchMemory.clear()
                catchMemory.addAll(news.map { it })
            }catch (e: Exception){
                //TODO
            }

        } else {
            //TODO
        }
        return result
    }

    override suspend fun getAllCachedNews(): Result<List<Article>> {
//        return newsDao.getAll().let {
//            Result.success(it)
//        }
        return if (catchMemory.isNotEmpty()){
            Result.success(catchMemory)
        }else {
            val cachedNews = newsDao.getAll()
            catchMemory.clear()
            catchMemory.addAll(cachedNews)
            Result.success(cachedNews)
        }
    }

    override suspend fun addToFavorites(articleId: Int) {
//        Log.d("NewsRepository", "Adding article with ID: $articleId to favorites")
//        newsDao.addToFavourites(articleId)
        Log.d("NewsRepository", "Adding article with ID: $articleId to favorites")
        newsDao.addToFavourites(articleId)
        // Log to check the updated database state
        val updatedFavorites = newsDao.getAllFavorites()
        Log.d("NewsRepository", "Updated favorites in DB: $updatedFavorites")
    }

    override suspend fun removeFavorites(articleId: Int) {
        newsDao.removeFavourites(articleId)
    }

    override suspend fun getAllFavorites(): List<Article> {
        val favorites = newsDao.getAllFavorites()
        Log.d("NewsRepository", "Fetched favorites from DB: $favorites")
        return favorites
    }

}