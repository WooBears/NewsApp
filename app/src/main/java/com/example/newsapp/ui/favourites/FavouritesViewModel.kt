package com.example.newsapp.ui.favourites

import android.util.Log
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
class FavouritesViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    private val _favorites = MutableLiveData<List<Article>>()
    val favorites: MutableLiveData<List<Article>> = _favorites

    fun removeFavorites(articleId: Int){
        viewModelScope.launch (Dispatchers.IO){
            repository.removeFavorites(articleId)
            fetchFavorites()
        }
    }

    fun fetchFavorites(){
        viewModelScope.launch(Dispatchers.IO) {
            val favArticles = repository.getAllFavorites()
            _favorites.postValue(favArticles)
        }
    }
}