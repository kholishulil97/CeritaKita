package com.example.storyapp.view.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.storyapp.R
import com.example.storyapp.data.local.StoryEntity
import com.example.storyapp.data.remote.response.story.ListStoryItem
import com.example.storyapp.databinding.ItemStoryBinding
import com.example.storyapp.view.main.detailstory.DetailStoryActivity
import com.example.storyapp.view.main.detailstory.DetailStoryActivity.Companion.EXTRA_DATA

class StoryAdapter : ListAdapter<ListStoryItem, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val news = getItem(position)
        holder.bind(news)
    }

    class MyViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(story: ListStoryItem) {
            binding.titleTextView.text = story.name
            binding.descTextView.text = story.description
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_error))
                .into(binding.storyImageView)
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                intent.putExtra(EXTRA_DATA, story)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.storyImageView, "story"),
                        Pair(binding.titleTextView, "name"),
                        Pair(binding.descTextView, "desc")
                    )
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ListStoryItem> =
            object : DiffUtil.ItemCallback<ListStoryItem>() {
                override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                    return oldItem.id == newItem.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                    return oldItem == newItem
                }
            }
    }
}

