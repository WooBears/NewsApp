package com.example.newsapp.ui.home

import android.content.res.Configuration
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentHomeBinding
import com.example.newsapp.ui.adapter.NewsAdapter
import com.example.newsapp.domain.model.Article
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private val newsAdapter: NewsAdapter by lazy { NewsAdapter(this::onClick) }
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.rvRecyclerView.adapter = newsAdapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpChipGroup()

        newsAdapter.addLoadStateListener { loadState ->
            // show or hide progress bar based on the loading state
            val isLoading = loadState.append is LoadState.Loading
            binding.progressbar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // restore state or set default chip
        if (viewModel.currentCategory == null) {
            setDefaultChip()
        } else {
            restoreChipSelection(viewModel.currentCategory!!)
        }
    }


    private fun fetchNewsByCategory(category: String) {
        if (category != viewModel.currentCategory) {
            viewModel.currentCategory = category // save category in ViewModel
            isLoading = true

            viewModel.getPagingNewsByCategory(category).observe(viewLifecycleOwner) { result ->
                lifecycleScope.launch {
                    result?.let {
                        newsAdapter.submitData(it)
                        isLoading = false
                    }
                }
            }
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

        selectedChip.isChecked = true
        selectedChip.setChipBackgroundColorResource(R.color.chip_selected)
    }

    private fun setDefaultChip() {
        val defaultChip = binding.tvGeneral
        fetchNewsByCategory(defaultChip.text.toString().lowercase())
        updateChipSelection(defaultChip)
        updateChipTextColor()
    }

    private fun restoreChipSelection(category: String) {
        val chipCategoryMap = mapOf(
            binding.tvGeneral to "general",
            binding.tvBusiness to "business",
            binding.tvSport to "sport",
            binding.tvTechnology to "technology",
            binding.tvEntertainment to "entertainment"
        )

        chipCategoryMap.forEach { (chip, chipCategory) ->
            if (chipCategory == category) {
                fetchNewsByCategory(chipCategory)
                updateChipSelection(chip)
            }
        }
        updateChipTextColor()
    }

    private fun setUpChipGroup() {
        val chipCategoryMap = mapOf(
            binding.tvGeneral to "general",
            binding.tvBusiness to "business",
            binding.tvSport to "sport",
            binding.tvTechnology to "technology",
            binding.tvEntertainment to "entertainment"
        )

        chipCategoryMap.forEach { (chip, category) ->
            chip.setOnClickListener {
                fetchNewsByCategory(category)
                updateChipSelection(chip)
            }
        }
        updateChipTextColor()
    }

    private fun updateChipTextColor() {
        val chipTextColor = if (isDarkMode()) {
            ContextCompat.getColor(requireContext(), R.color.chip_text_light)
        } else {
            ContextCompat.getColor(requireContext(), R.color.chip_text_dark)
        }

        val chips = listOf(
            binding.tvGeneral,
            binding.tvBusiness,
            binding.tvSport,
            binding.tvTechnology,
            binding.tvEntertainment
        )

        chips.forEach { chip ->
            chip.setTextColor(chipTextColor)
        }
    }

    private fun onClick(article: Article) {
        val bundle = Bundle().apply {
            putParcelable("article", article)
        }

        val options = NavOptions.Builder()
            .setPopUpTo(R.id.homeFragment, false)
            .build()
        findNavController().navigate(R.id.detailsFragment, bundle, options)
    }

    private fun isDarkMode(): Boolean {
        return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}