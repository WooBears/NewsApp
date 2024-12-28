package com.example.newsapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.newsapp.domain.model.NewsResponse
import com.example.newsapp.domain.repository.NewsRepository
import com.example.newsapp.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NewsRepository
): ViewModel() {

    fun getNews(country: String) : LiveData<Result<NewsResponse>>{
        return liveData(Dispatchers.IO) {
            try {
                val result = repository.getNews(country)
                emit(result)
            }catch (e: Exception){
                emit(Result.error(e.message ?: "Unknown error"))
            }
        }
    }
}