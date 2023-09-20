package com.example.storyapp.view.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.data.Result
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.view.ViewModelFactory
import com.example.storyapp.view.login.LoginActivity
import com.example.storyapp.view.welcome.WelcomeActivity
import java.util.Timer
import kotlin.concurrent.schedule

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
        setupData()
        setupView()
    }

    private fun setupUser() {
        viewModel.getSession().observe(this) {
            token = it.token
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
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupData() {
        viewModel.getStories().observe(this@MainActivity) { result ->
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