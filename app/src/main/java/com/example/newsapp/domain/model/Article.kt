package com.example.newsapp.domain.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.parcelize.Parcelize

@Entity(tableName = "Article", indices = [Index(value = ["id"], unique = true)])
@TypeConverters(Converters::class)
@Parcelize
data class Article(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val source: Source,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?,
    @ColumnInfo(defaultValue = "0") val isFavorite: Boolean = false
) : Parcelable