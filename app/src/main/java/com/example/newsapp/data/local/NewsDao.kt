package com.example.newsapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.newsapp.domain.model.Article
import retrofit2.http.DELETE

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertALl(articles: List<Article>)

    @Query("SELECT * FROM Article")
    fun getAll() : List<Article>

    @Query("DELETE FROM Article")
    fun clearAll()

    @Query("UPDATE Article SET isFavorite = 1 WHERE id = :articleId")
    fun addToFavourites(articleId: Int)

    @Query("UPDATE Article SET isFavorite = 0 WHERE id = :articleId")
    fun removeFavourites(articleId: Int)

    @Query("SELECT * FROM ARTICLE WHERE isFavorite = 1")
    fun getAllFavorites(): List<Article>

    @Query("SELECT * FROM Article WHERE title")
    fun getSearchedResult(search: String) : List<Article>

}