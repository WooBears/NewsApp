package com.example.newsapp.ui.details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: NewsRepository
): ViewModel() {

    private val _favorites = MutableLiveData<List<Article>>()
    val favorites: LiveData<List<Article>> = _favorites

    fun addFavorites(articleId: Int){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addToFavorites(articleId)
            Log.d("DetailsViewmodel", "id $articleId")

            fetchFavorites()
        }
    }

    fun removeFavorites(articleId: Int){
        viewModelScope.launch (Dispatchers.IO){
            repository.removeFavorites(articleId)
        }
    }

    fun fetchFavorites(){
        viewModelScope.launch(Dispatchers.IO) {
            _favorites.postValue(repository.getAllFavorites())
        }
    }
}