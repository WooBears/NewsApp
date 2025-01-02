package com.example.newsapp.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.newsapp.data.remote.NewsApiService
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.repository.NewsRepository
class NewsPagingSource(
    private val newsRepository: NewsRepository
) : PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        return try {
            val currentPage = params.key ?: 1
            val response = newsRepository.getNews("us")

            // learning about this problem that I had more
            val articles = response.data?.mapIndexed { index, article ->
                article.copy(id = index + (currentPage - 1) * params.loadSize)
            } ?: emptyList()

            val nextKey = if (articles.isEmpty() || articles.size < params.loadSize) null else currentPage + 1

            LoadResult.Page(
                data = articles,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = nextKey
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition)
        return page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
    }
}