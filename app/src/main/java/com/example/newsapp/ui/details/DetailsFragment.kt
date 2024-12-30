package com.example.newsapp.ui.details

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentDetailsBinding
import com.example.newsapp.domain.model.Article
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private val viewModel: DetailsViewModel by viewModels()
    private lateinit var binding: FragmentDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            findNavController().navigate(R.id.homeFragment)
        }

        val article: Article? = arguments?.getParcelable("article")

        article?.let {
            Glide.with(binding.root).load(article.urlToImage).into(binding.ivPicDet)
            binding.tvAuthorDet.text = article.author
            binding.tvDateDet.text = article.publishedAt
            binding.tvDescriptionDes.text = article.description
            binding.tvContentDes.text = article.content
        }

        // Observe the favorites list to update the UI
        viewModel.favorites.observe(viewLifecycleOwner, Observer { favorites ->
            // Check if the current article is in the favorites list
            val isFavorite = favorites.any { it.id == article?.id }
            if (isFavorite) {
                //binding.ivStarDet.setImageResource(R.drawable.ic_star_filled)
            } else {
                //binding.ivStarDet.setImageResource(R.drawable.ic_star_empty)
            }
        })

        // Handle star icon click to add/remove from favorites
        binding.ivStarDet.setOnClickListener {

                // If it's not a favorite, add it
                Log.d("DetailsFragment", "Article ID: ${article?.id}")
                viewModel.addFavorites(article!!.id)
        }
    }
//        binding.ivStarDet.setOnClickListener {
//            viewModel.addFavorites(article!!.id)
//        }
}