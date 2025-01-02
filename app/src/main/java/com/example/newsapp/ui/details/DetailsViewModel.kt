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
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    private val _favorites = MutableLiveData<List<Article>>()
    val favorites: LiveData<List<Article>> = _favorites

    // adding data to fav and updating
    fun addFavorites(articleId: Int) {
        viewModelScope.launch(Dispatchers.IO) {

            repository.addToFavorites(articleId)
            updateFavoriteState()
        }
    }

    // rem data to fav and updating
    fun removeFavorites(articleId: Int) {
        viewModelScope.launch(Dispatchers.IO) {

            repository.removeFavorites(articleId)
            updateFavoriteState()
        }
    }


    fun fetchFavorites() {
        updateFavoriteState()
    }

    private fun updateFavoriteState() {
        viewModelScope.launch {
            val favorites = withContext(Dispatchers.IO) {
                repository.getAllFavorites()
            }
            _favorites.value = favorites
        }
    }
}