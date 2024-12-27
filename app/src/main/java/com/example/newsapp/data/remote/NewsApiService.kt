package com.example.newsapp.data.remote

import com.example.newsapp.domain.model.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("top-headlines?country=us")
    suspend fun getNews() : Response<NewsResponse>
}