package com.example.storyapp.view.main

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.storyapp.R
import com.example.storyapp.data.remote.response.story.ListStoryItem
import com.example.storyapp.databinding.ItemStoryBinding
import com.example.storyapp.view.main.detailstory.DetailStoryActivity
import com.example.storyapp.view.main.detailstory.DetailStoryActivity.Companion.EXTRA_DATA

class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(STORY_DIFF_CALLBACK) {

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val tile = getItem(position)
        if (tile != null) {
            holder.bind(tile)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding =
            ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    class StoryViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            binding.apply {
                titleTextView.text = story.name
                descTextView.text = story.description
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .fitCenter()
                    .apply(
                        RequestOptions
                            .placeholderOf(R.drawable.ic_loading)
                            .error(R.drawable.ic_error)
                    ).into(storyImageView)

                itemView.setOnClickListener {
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(storyImageView, "story"),
                            Pair(titleTextView, "name"),
                            Pair(descTextView, "desc")
                        )
                    val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                    intent.putExtra(EXTRA_DATA, story)
                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }

    companion object {
        private val STORY_DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean =
                oldItem == newItem
        }
    }
}

