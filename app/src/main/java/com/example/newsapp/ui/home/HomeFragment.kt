package com.example.newsapp.ui.home

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentHomeBinding
import com.example.newsapp.domain.adapter.NewsAdapter
import com.example.newsapp.domain.model.Article
import com.example.newsapp.util.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var newsAdapter: NewsAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsAdapter = NewsAdapter(this::onClick)
        binding.rvRecyclerView.adapter = newsAdapter

        bindNews("us")

    }

    private fun bindNews(country: String) = lifecycleScope.launch (Dispatchers.Main) {
        viewModel.getNews(country).observe(viewLifecycleOwner){result ->

            try {
                when(result.status){
                    Result.Status.SUCCESS -> {
                        collectNewsList()
                    }
                    Result.Status.ERROR -> {
                        bindCachedNews()
                    }
                    Result.Status.LOADING -> {

                    }
                }
            }catch (e: Exception){
                bindCachedNews()
            }

        }
    }
    private fun bindCachedNews() {

        viewModel.getAllCachedNews().observe(viewLifecycleOwner) { result ->
            when (result.status) {
                Result.Status.SUCCESS -> {
                    val cachedNews = result.data
                    if (!cachedNews.isNullOrEmpty()) {
                        // If cached data exists, submit it to the adapter inside a coroutine
                        lifecycleScope.launch {
                            newsAdapter.submitData(PagingData.from(cachedNews))
                            Log.d("CachedNews", "Cached data: ${cachedNews.size} articles")
                        }
                    } else {

                    }
                }
                Result.Status.ERROR -> {

                }
                Result.Status.LOADING -> {

                }
            }
        }
    }
    private fun collectNewsList(){
        lifecycleScope.launch {
            viewModel.newsList.collectLatest { pagingData ->
                newsAdapter.submitData(pagingData)
            }
        }
    }
    private fun onClick(article: Article){
        val bundle = Bundle().apply {
            putParcelable("article", article)
        }
        findNavController().navigate(R.id.detailsFragment, bundle)
    }
}