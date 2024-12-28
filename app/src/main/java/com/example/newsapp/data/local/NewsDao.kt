package com.example.newsapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.newsapp.domain.model.Article

@Dao
interface NewsDao {

    @Insert
    suspend fun insertALl(articles: List<Article>)

    @Query("SELECT * FROM Article")
    suspend fun getAll(articles: List<Article>)
}