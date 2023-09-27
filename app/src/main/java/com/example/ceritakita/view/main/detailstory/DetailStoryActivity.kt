package com.example.ceritakita.view.main.detailstory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ceritakita.R
import com.example.ceritakita.data.remote.response.story.ListStoryItem
import com.example.ceritakita.databinding.ActivityDetailStoryBinding

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

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
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
            Glide.with(this@DetailStoryActivity)
                .load(R.drawable.storm_trooper)
                .fitCenter()
                .circleCrop()
                .apply(
                    RequestOptions
                        .placeholderOf(R.drawable.ic_loading)
                        .error(R.drawable.ic_error)
                ).into(imageViewAvatar)
        }
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}