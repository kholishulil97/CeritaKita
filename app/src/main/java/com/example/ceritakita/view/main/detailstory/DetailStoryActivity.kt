package com.example.ceritakita.view.main.detailstory

import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ceritakita.R
import com.example.ceritakita.data.entity.ListStoryItem
import com.example.ceritakita.databinding.ActivityDetailStoryBinding
import com.example.ceritakita.utils.getAddress
import java.util.Locale

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
        try {
            val lat = data.lat
            val lon = data.lon
            Geocoder(this, Locale.getDefault())
                .getAddress(lat, lon) { address: android.location.Address? ->
                    if (address != null) {
                        binding.labelLocation.text = address.getAddressLine(0)
                        binding.labelLocation.isVisible = true
                    } else {
                        binding.labelLocation.isVisible = true
                    }
                }
        } catch (e: Exception) {
            binding.labelLocation.isVisible = false
        }
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}