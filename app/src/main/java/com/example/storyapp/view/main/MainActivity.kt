package com.example.storyapp.view.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.data.Result
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.view.ViewModelFactory
import com.example.storyapp.view.addstory.AddStoryActivity
import com.example.storyapp.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter
    private var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUser()
        setupAdapter()
        setupView()
    }

    private fun setupUser() {
        viewModel.getSession().observe(this) {
            token = it.token
            setupData(token)
        }
    }

    private fun setupView() {
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.btn_language -> {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    true
                }
                R.id.btn_logout -> {
                    viewModel.logout()
                    val intent = Intent(this, WelcomeActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }

        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupData(token: String) {
        viewModel.getStories(token).observe(this@MainActivity) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBarStory.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBarStory.visibility = View.GONE
                        val storyData = result.data
                        storyAdapter.submitList(storyData.listStory)
                    }
                    is Result.Error -> {
                        binding.progressBarStory.visibility = View.GONE
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.message_dialog_server_error) + result.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {}
                }
            }
        }
        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = storyAdapter
        }
    }

    private fun setupAdapter() {
        storyAdapter = StoryAdapter()
    }
}