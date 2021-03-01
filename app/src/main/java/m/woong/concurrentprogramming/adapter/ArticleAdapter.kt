package m.woong.concurrentprogramming.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import m.woong.concurrentprogramming.R
import m.woong.concurrentprogramming.databinding.ArticleBinding
import m.woong.concurrentprogramming.model.Article

class ArticleAdapter : ListAdapter<Article, ArticleAdapter.ViewHolder>(DIFF_UTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleAdapter.ViewHolder {
        val binding = DataBindingUtil.inflate<ArticleBinding>(
            LayoutInflater.from(parent.context),
            R.layout.article,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleAdapter.ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class ViewHolder(
        private val binding: ArticleBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(article: Article){
            with(binding) {
                this.article = article
                executePendingBindings()
            }
        }
    }

    companion object{
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<Article>(){
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean =
                oldItem.title == newItem.title

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean =
                oldItem == newItem

        }
    }
}