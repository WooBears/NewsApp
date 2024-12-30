package com.example.newsapp.domain.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.databinding.NewsItemBinding
import com.example.newsapp.domain.model.Article

class FavoriteAdapter : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    private var items = arrayListOf<Article>()

    fun addNews(list: List<Article>){
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoriteAdapter.FavoriteViewHolder {
        return FavoriteViewHolder(
            NewsItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: FavoriteAdapter.FavoriteViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }


    inner class FavoriteViewHolder(private var binding: NewsItemBinding) : RecyclerView.ViewHolder(binding.root){

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

        }
    }
}