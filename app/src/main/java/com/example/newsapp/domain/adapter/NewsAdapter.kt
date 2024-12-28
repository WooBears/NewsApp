package com.example.newsapp.domain.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
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
) :  RecyclerView.Adapter<NewsAdapter.NewsViewHolder>(){

    private var items = arrayListOf<Article>()

    fun addNews(list: List<Article>){
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            NewsItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
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

}