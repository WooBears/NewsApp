package com.example.newsapp.ui.favourites

import android.app.AlertDialog
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentFavouritesBinding
import com.example.newsapp.domain.adapter.FavoriteAdapter
import com.example.newsapp.domain.adapter.NewsAdapter
import com.example.newsapp.domain.model.Article
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavouritesFragment : Fragment() {

    private val viewModel: FavouritesViewModel by viewModels()
    //private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var binding: FragmentFavouritesBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //favoriteAdapter = FavoriteAdapter(this::onLongClick)
        newsAdapter = NewsAdapter(this::onLongClick)
        binding.rvRecyclerView.adapter = newsAdapter

        viewModel.favorites.observe(viewLifecycleOwner, Observer { favorites ->

            lifecycleScope.launch(Dispatchers.IO) {
                newsAdapter.submitData(PagingData.from(favorites))
            }
        })

        viewModel.fetchFavorites()
    }

    private fun onLongClick(article: Article){
        AlertDialog.Builder(requireContext())
            .setTitle("Remove Favorite")
            .setMessage("Do you want to remove the ${article.title} from favorites?")
            .setPositiveButton("Yes"){_,_ ->
                viewModel.removeFavorites(article.id)
            }
            .setNegativeButton("No", null)
            .show()
    }
}