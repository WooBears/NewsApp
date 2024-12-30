package com.example.newsapp.domain.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.databinding.NewsItemBinding
import com.example.newsapp.domain.model.Article
import java.time.format.DateTimeFormatter
import java.util.Locale

class NewsAdapter(
    private val onClick: (Article) -> Unit
) :  PagingDataAdapter< Article,NewsAdapter.NewsViewHolder>(NewsDiffCallBack()){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            NewsItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
//        val article = holder.bind(getItem(position)!!)
//        Log.d("AdapterBinding", "Binding article: $article")
        //holder.setIsRecyclable(false)
        val article = getItem(position)
        Log.d("AdapterBinding", "Binding article: $article")
        if (article != null) {
            holder.bind(article)
        }
    }

    inner class NewsViewHolder(private var binding: NewsItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(article: Article){

            if (!article.urlToImage.isNullOrEmpty()){
                Glide.with(binding.root)
                    .load(article.urlToImage)
                    .into(binding.ivNewsImage)
            }else {
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

    class NewsDiffCallBack : DiffUtil.ItemCallback<Article>() {

        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

    }

}