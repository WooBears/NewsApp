package com.example.newsapp.data.remoteMediato

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.newsapp.data.local.NewsDao
import com.example.newsapp.data.local.NewsDatabase
import com.example.newsapp.data.remote.NewsApiService
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.model.Source
import retrofit2.HttpException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class NewsRemoteMediator @Inject constructor(
    private val apiService: NewsApiService,
    private val newsDatabase: NewsDatabase,
    private val category: String
) : RemoteMediator<Int, Article>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Article>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    1
                }
            }

            val response = apiService.getNewsByCategory(category, page)
            if (response.isSuccessful && response.body() != null) {
                val articles = response.body()!!.articles

                newsDatabase.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        newsDatabase.newsDao().clearAll()
                    }
                    newsDatabase.newsDao().insertALl(articles)
                    Log.d(
                        "NewsRemoteMediator",
                        "Inserted ${articles.size} articles into the database"
                    )
                }
                MediatorResult.Success(endOfPaginationReached = articles.isEmpty())
            } else {
                MediatorResult.Error(HttpException(response))
            }
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
