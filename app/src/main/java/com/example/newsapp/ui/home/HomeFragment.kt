package com.example.newsapp.ui.home

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentHomeBinding
import com.example.newsapp.domain.adapter.NewsAdapter
import com.example.newsapp.domain.model.Article
import com.example.newsapp.util.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.zip.Inflater

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

        bindUi("us")

    }

    private fun bindUi(country: String) = lifecycleScope.launch (Dispatchers.Main){
        try {
            val news = viewModel.getNews(country)
            news.observe(viewLifecycleOwner, Observer { result ->

                when(result.status){
                    Result.Status.SUCCESS -> {
                        result.data?.let { news ->
                            newsAdapter.addNews(news.articles)
                        }
                    }
                    Result.Status.ERROR -> {
                        Log.e("HomeFragment", "API Call Error: ${result.message}")
                    }
                    Result.Status.LOADING -> {
                        Log.d("HomeFragment", "API Call Loading")
                    }
                }
            })
        }catch (e: Exception){
            Log.e("HomeFragment", "exception occurred: ${e.message}")
        }
    }

    private fun onClick(article: Article){
        val bundle = Bundle().apply {
            putParcelable("article", article)
        }
        findNavController().navigate(R.id.detailsFragment, bundle)
    }
}