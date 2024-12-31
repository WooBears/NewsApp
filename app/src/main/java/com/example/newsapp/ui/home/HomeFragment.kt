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
import com.google.android.material.chip.Chip
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
        setUpChipGroup()

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

    private fun setUpChipGroup() {

        binding.tvGeneral.setOnClickListener {
            Log.d("HomeFragment", "General chip clicked")
            fetchNewsByCategory("general")
        }
        binding.tvBusiness.setOnClickListener {
            Log.d("HomeFragment", "Bus chip clicked")
            fetchNewsByCategory("business")
        }
        binding.tvSport.setOnClickListener {
            Log.d("HomeFragment", "spo chip clicked")
            fetchNewsByCategory("sport")
        }
        binding.tvEducation.setOnClickListener {
            Log.d("HomeFragment", "edu chip clicked")
            fetchNewsByCategory("education")
        }
        binding.tvTechnology.setOnClickListener {
            Log.d("HomeFragment", "tech chip clicked")
            fetchNewsByCategory("technology")
        }
        binding.tvEntertainment.setOnClickListener {
            Log.d("HomeFragment", "enter chip clicked")
            fetchNewsByCategory("entertainment")
        }
   }

    private fun fetchNewsByCategory(category: String) {
        Log.d("HomeFragment", "Fetching news for category: $category")
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getNewsByCategory(category).observe(viewLifecycleOwner) { result ->
                when (result.status) {
                    Result.Status.SUCCESS -> {
                        val articles = result.data
                        if (!articles.isNullOrEmpty()) {
                            lifecycleScope.launch(Dispatchers.IO) {
                                newsAdapter.submitData(PagingData.from(articles))
                            }
                        } else {
                            Log.d("HomeFragment", "No articles found for category: $category")
                        }
                    }
                    Result.Status.ERROR -> {
                        Log.e("HomeFragment", "Error fetching news for category: $category")
                    }
                    Result.Status.LOADING -> {
                        Log.d("HomeFragment", "Loading news for category: $category")
                    }
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