package com.example.newsapp.domain.model

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)

