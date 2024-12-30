package com.example.newsapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
) : Parcelable

