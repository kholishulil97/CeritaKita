package com.example.storyapp.view.main.detailstory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.storyapp.R
import com.example.storyapp.data.remote.response.story.ListStoryItem
import com.example.storyapp.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_story)

        setupView()
        setupData()
    }

    private fun setupView() {
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = getString(R.string.label_detail_story)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupData() {
        val data = intent.getParcelableExtra<ListStoryItem>(EXTRA_DATA) as ListStoryItem
        binding.apply {
            nameTextView.text = data.name
            descTextView.text = data.description
            Glide.with(this@DetailStoryActivity)
                .load(data.photoUrl)
                .fitCenter()
                .apply(
                    RequestOptions
                        .placeholderOf(R.drawable.ic_loading)
                        .error(R.drawable.ic_error)
                ).into(profileImageView)
        }
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}