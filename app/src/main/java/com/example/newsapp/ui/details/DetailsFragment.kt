package com.example.newsapp.ui.details

import android.content.res.Configuration
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentDetailsBinding
import com.example.newsapp.domain.model.Article
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private lateinit var binding: FragmentDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(this).get(DetailsViewModel::class.java)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        val article: Article? = arguments?.getParcelable("article")

        article?.let {
            Glide.with(binding.root).load(article.urlToImage).into(binding.ivPicDet)
            binding.tvAuthorDet.text = article.author
            binding.tvDateDet.text = article.publishedAt
            binding.tvDescriptionDes.text = article.description
            binding.tvContentDes.text = article.content

            viewModel.fetchFavorites()
        }

        viewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            val currentArticleId = article?.id ?: -1

            // check if the current article is in favorites
            val isFavorite = favorites.any { it.id == currentArticleId }

            binding.ivStarDet.setImageResource(
                if (isFavorite) R.drawable.bookmark2 else R.drawable.bookmark1
            )

            val iconColor = if (isDarkMode()) {
                ContextCompat.getColor(requireContext(), R.color.icon_color_dark)
            } else {
                ContextCompat.getColor(requireContext(), R.color.icon_color_light)
            }

            binding.ivStarDet.setColorFilter(iconColor)
        }

        binding.ivStarDet.setOnClickListener {
            article?.let { currentArticle ->
                val currentFavorites = viewModel.favorites.value.orEmpty()
                val isCurrentlyFavorite = currentFavorites.any { it.id == currentArticle.id }

                if (isCurrentlyFavorite) {
                    viewModel.removeFavorites(currentArticle.id)
                    Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    viewModel.addFavorites(currentArticle.id)
                    Toast.makeText(requireContext(), "Added to favorites", Toast.LENGTH_SHORT)
                        .show()
                }
            } ?: Log.e("DetailsFragment", "Article is null, cannot add/remove from favorites")
        }

    }

    private fun isDarkMode(): Boolean {
        return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}
