package com.example.storyapp.view.main.detailstory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.storyapp.R

class DetailStoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_story)
    }

    companion object {
        const val EXTRA_DATA = "extra_data"
    }
}