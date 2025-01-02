package com.example.newsapp.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.repository.NewsRepository
import com.example.newsapp.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    fun getSearched(search: String) : LiveData<Result<List<Article>>>{
        return liveData (Dispatchers.IO){
            try {
                val result = newsRepository.getSearchedResult(search)

                emit(result)
            }catch (e: Exception){

                emit(Result.error(e.message ?: "Unknown error"))
            }
        }
    }
}