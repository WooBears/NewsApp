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
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
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
        binding.ivStarDet.setImageResource(R.drawable.bookmark1)


        viewModel.favorites.observe(viewLifecycleOwner, Observer { favorites ->

            val isFavorite = favorites.any { it.id == article?.id }

            if (isFavorite) {
                binding.ivStarDet.setImageResource(R.drawable.bookmark2)
            } else {
                binding.ivStarDet.setImageResource(R.drawable.bookmark1)
            }
        })

        binding.ivStarDet.setOnClickListener {
            article?.let { article ->

                if (viewModel.favorites.value?.any { it.id == article.id } == true) {

                    viewModel.removeFavorites(article.id)
                } else {

                    viewModel.addFavorites(article.id)
                }
            }
        }
    }
}