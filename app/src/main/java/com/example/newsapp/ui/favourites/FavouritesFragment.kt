package com.example.newsapp.ui.favourites

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentFavouritesBinding
import com.example.newsapp.domain.adapter.FavoriteAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouritesFragment : Fragment() {

    private val viewModel: FavouritesViewModel by viewModels()
    private lateinit var favoriteAdapter: FavoriteAdapter
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

        favoriteAdapter = FavoriteAdapter()
        binding.rvRecyclerView.adapter = favoriteAdapter

        viewModel.favorites.observe(viewLifecycleOwner, Observer { favorites ->

            Log.d("FavouritesFragment", "Favorites updated: $favorites")

            favoriteAdapter.addNews(favorites)
        })

        viewModel.fetchFavorites()
    }
}