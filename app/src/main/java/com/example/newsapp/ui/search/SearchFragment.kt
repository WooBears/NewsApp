package com.example.newsapp.ui.search

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentSearchBinding
import com.example.newsapp.domain.adapter.NewsAdapter
import com.example.newsapp.domain.model.Article
import com.example.newsapp.util.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModels()
    private lateinit var binding: FragmentSearchBinding
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsAdapter = NewsAdapter(this::onClick)
        binding.rvRecycler.adapter = newsAdapter

        // search by title
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let { search(it.toString()) }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }

    private fun search(search: String) {

        viewModel.getSearched(search).observe(viewLifecycleOwner) { result ->
            result.data?.let { article ->
                if (result.status == Result.Status.SUCCESS) {
                    lifecycleScope.launch {
                        newsAdapter.submitData(PagingData.from(article))
                    }
                } else {
                    Toast.makeText(requireContext(), "Something went wrong, Error: ${result.message} ", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(requireContext(), "No data found or error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onClick(article: Article) {
        val bundle = Bundle().apply {
            putParcelable("article", article)
        }
        findNavController().navigate(R.id.detailsFragment, bundle)
    }
}