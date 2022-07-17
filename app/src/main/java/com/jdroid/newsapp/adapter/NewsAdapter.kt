package com.jdroid.newsapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jdroid.newsapp.api.Article
import com.jdroid.newsapp.databinding.ListItemNewsBinding

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(ListItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class NewsViewHolder(private val mBinding: ListItemNewsBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind() {
            mBinding.apply {
                val article = differ.currentList[adapterPosition]
                Glide.with(mBinding.root.context).load(article.urlToImage).into(ivArticleImage)
                tvTitle.text = article.title
                tvDescription.text = article.description
                tvPublishedAt.text = article.publishedAt
                tvSource.text = article.source?.name
                itemView.setOnClickListener {
                    onItemClickListener?.let {
                        it(article)
                    }
                }

                itemView.setOnLongClickListener {
                    onItemLongClickListener?.let {
                        it(article)
                    }
                    true
                }
            }
        }
    }

    private var onItemClickListener: ((Article) -> Unit)? = null
    private var onItemLongClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnLongClickListener(listener: (Article) -> Unit) {
        onItemLongClickListener = listener
    }
}