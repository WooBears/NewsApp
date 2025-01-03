package com.example.newsapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.newsapp.data.repository.NewsPagingSource
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.model.NewsResponse
import com.example.newsapp.domain.repository.NewsRepository
import com.example.newsapp.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale.Category
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NewsRepository
): ViewModel() {

    val newsList = Pager(
        PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        )
    ){
        NewsPagingSource(repository)
    }.flow.cachedIn(viewModelScope)

    fun getNews(country: String) : LiveData<Result<List<Article>>>{

        return liveData(Dispatchers.IO) {
            emit(Result.loading())
            try {
                val result = repository.getNews(country)
                emit(result)
            }catch (e: Exception){
                emit(Result.error(e.message ?: "Unknown error"))
            }
        }
    }

    fun getAllCachedNews() : LiveData<Result<List<Article>>>{
        return liveData (Dispatchers.IO) {
            emit(Result.loading())
            try {
                val result = repository.getAllCachedNews()
                emit(result)
            }catch (e: Exception){
                emit(Result.error(e.message ?: "Unknown error"))
            }
        }
    }

    fun getNewsByCategory(category: String) : LiveData<Result<List<Article>>>{
        return liveData (Dispatchers.IO){
            try {
                val result = repository.getNewsByCategory(category)
                emit(result)
            }catch (e: Exception){
                emit(Result.error(e.message ?: "Unknown error "))
            }
        }
    }

}