package com.example.newsapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.newsapp.domain.model.Article

@Database(entities = [Article::class], version = 2)
abstract class NewsDatabase : RoomDatabase(){
    abstract fun newsDao() : NewsDao
}