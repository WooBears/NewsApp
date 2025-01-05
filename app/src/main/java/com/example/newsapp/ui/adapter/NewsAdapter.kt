package com.example.newsapp.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.databinding.ItemLoadingBinding
import com.example.newsapp.databinding.NewsItemBinding
import com.example.newsapp.domain.model.Article
import java.time.format.DateTimeFormatter
import java.util.Locale

class NewsAdapter(
    private val onClick: (Article) -> Unit
) : PagingDataAdapter<Article, RecyclerView.ViewHolder>(NewsDiffCallBack()) {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position) == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_LOADING -> {
                val binding =
                    ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LoadingViewHolder(binding)
            }

            else -> {
                val binding =
                    NewsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                NewsViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NewsViewHolder -> {
                val article = getItem(position)
                article?.let { holder.bind(it) }
            }

            is LoadingViewHolder -> {

            }
        }
    }

    inner class NewsViewHolder(private var binding: NewsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            if (!article.urlToImage.isNullOrEmpty()) {
                Glide.with(binding.root)
                    .load(article.urlToImage)
                    .into(binding.ivNewsImage)
            } else {
                binding.ivNewsImage.setImageResource(R.drawable.iv_corners)
            }

            binding.tvAuthor.text = article.author ?: "No Author"
            binding.tvDescription.text = article.description ?: "No Content"
            binding.tvDate.text = article.publishedAt

            binding.root.setOnClickListener {
                onClick(article)
            }
        }
    }

    inner class LoadingViewHolder(binding: ItemLoadingBinding) :
        RecyclerView.ViewHolder(binding.root)

    class NewsDiffCallBack : DiffUtil.ItemCallback<Article>() {

        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
}