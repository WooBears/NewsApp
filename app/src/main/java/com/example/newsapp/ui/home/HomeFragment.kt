package com.example.newsapp.ui.home

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentHomeBinding
import com.example.newsapp.ui.adapter.NewsAdapter
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
    private var currentCategory: String = "us"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsAdapter = NewsAdapter(this::onClick)
        binding.rvRecyclerView.adapter = newsAdapter


        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.resetDataLoaded()
            bindNews(currentCategory)
        }

        // check if data is loaded, if not call bindNews, else bindCachedNews
        if (!viewModel.isDataLoaded) {
            bindNews(currentCategory)
        } else {
            bindCachedNews()
        }

        setUpChipGroup()
        setDefaultChip()

    }

    private fun bindNews(country: String) = lifecycleScope.launch(Dispatchers.Main) {
        viewModel.getNews(country).observe(viewLifecycleOwner) { result ->

            try {
                when (result.status) {
                    Result.Status.SUCCESS -> {
                        binding.progressBarBannerDown.visibility = View.GONE
                        collectNewsList()
                    }

                    Result.Status.ERROR -> {
                        binding.progressBarBannerDown.visibility = View.GONE
                        bindCachedNews()
                    }

                    Result.Status.LOADING -> {
                        binding.progressBarBannerDown.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                binding.progressBarBannerDown.visibility = View.GONE
                bindCachedNews()
            }

            binding.swipeRefreshLayout.isRefreshing = false

        }
    }

    private fun bindCachedNews() {

        viewModel.getAllCachedNews().observe(viewLifecycleOwner) { result ->
            when (result.status) {
                Result.Status.SUCCESS -> {
                    binding.progressBarBannerDown.visibility = View.GONE
                    val cachedNews = result.data
                    if (!cachedNews.isNullOrEmpty()) {
                        // if cached data exists, submit it to the adapter
                        lifecycleScope.launch {
                            newsAdapter.submitData(PagingData.from(cachedNews))
                        }
                    } else {

                    }
                }

                Result.Status.ERROR -> {
                    binding.progressBarBannerDown.visibility = View.GONE
                }

                Result.Status.LOADING -> {
                    binding.progressBarBannerDown.visibility = View.VISIBLE
                }
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }
    private fun updateChipSelection(selectedChip: Chip) {
        val chips = listOf(
            binding.tvGeneral,
            binding.tvBusiness,
            binding.tvSport,
            binding.tvTechnology,
            binding.tvEntertainment
        )

        chips.forEach { chip ->
            chip.isChecked = false
            chip.setChipBackgroundColorResource(R.color.chip_unselected)
        }

        // Mark the selected chip as checked and set its background to selected
        selectedChip.isChecked = true
        selectedChip.setChipBackgroundColorResource(R.color.chip_selected)
    }

    private fun setDefaultChip() {
        // Set a default chip to be selected when the fragment is first created
        val defaultChip = binding.tvGeneral
        updateChipSelection(defaultChip)
    }

    private fun setUpChipGroup() {
        binding.apply {
            val chips = listOf(tvGeneral, tvBusiness, tvSport, tvTechnology, tvEntertainment)

            chips.forEach { chip ->
                chip.setOnClickListener {
                    fetchNewsByCategory(chip.text.toString().lowercase())
                    updateChipSelection(chip)
                }
            }
        }
    }

//    private fun updateChipSelection(selectedChip: Chip) {
//        val chips = listOf(
//            binding.tvGeneral,
//            binding.tvBusiness,
//            binding.tvSport,
//            binding.tvTechnology,
//            binding.tvEntertainment
//        )
//        chips.forEach { chip ->
//            chip.isChecked = false
//            chip.setChipBackgroundColorResource(R.color.chip_unselected)
//        }
//
//        selectedChip.isChecked = true
//        selectedChip.setChipBackgroundColorResource(R.color.chip_selected)
//    }

    private fun fetchNewsByCategory(category: String) {
        3
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
                        binding.progressBarBannerDown.visibility = View.GONE
                    }

                    Result.Status.ERROR -> {
                        binding.progressBarBannerDown.visibility = View.GONE
                    }

                    Result.Status.LOADING -> {
                        binding.progressBarBannerDown.visibility = View.VISIBLE
                    }

                }
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun collectNewsList() {
        lifecycleScope.launch {
            viewModel.newsList.collectLatest { pagingData ->
                newsAdapter.submitData(pagingData)
            }
        }
    }

    private fun onClick(article: Article) {
        findNavController().popBackStack()
        val bundle = Bundle().apply {
            putParcelable("article", article)
        }
        findNavController().navigate(R.id.detailsFragment, bundle)
    }
}