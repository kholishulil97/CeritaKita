package com.example.ceritakita.view.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.ceritakita.R
import com.example.ceritakita.data.Result
import com.example.ceritakita.data.paging.LoadingStateAdapter
import com.example.ceritakita.data.paging.StoryListAdapter
import com.example.ceritakita.databinding.ActivityMainBinding
import com.example.ceritakita.view.ViewModelFactory
import com.example.ceritakita.view.addstory.AddStoryActivity
import com.example.ceritakita.view.main.withlocation.StoryLocationActivity
import com.example.ceritakita.view.welcome.WelcomeActivity
import java.util.Timer
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryListAdapter
    private var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUser()
        setupAdapter()

        binding.rvStory.layoutManager = LinearLayoutManager(this)

        setupView()
    }

    private fun setupAdapter() {
        storyAdapter = StoryListAdapter()
    }

    private fun setupUser() {
        viewModel.getSession().observe(this) {
            token = it.token
            if (token.isEmpty()) {
                showFailedDialog("token")
            } else {
                getData()
            }
        }
    }

    private fun setupView() {
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.btn_map -> {
                    val intent = Intent(this, StoryLocationActivity::class.java)
                    startActivity(intent)
                    true
                }
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

        binding.swipeRefresh.setOnRefreshListener {
            onRefresh()
        }
    }

    private fun getData() {
        binding.rvStory.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter =
                storyAdapter.withLoadStateFooter(footer = LoadingStateAdapter { storyAdapter.retry() })
        }
        viewModel.story.observe(this) {
            storyAdapter.submitData(lifecycle, it)
        }
    }

    private fun showFailedDialog(error: String) {
        val mBuilder = AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.title_dialog_login_failed))
            setMessage(getString(R.string.message_dialog_server_response) + error)
            setPositiveButton(getString(R.string.positive_button_dialog_failed), null)
        }
        val mAlertDialog = mBuilder.create()
        mAlertDialog.show()
        val mPositiveButton = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        mPositiveButton.setOnClickListener {
            mAlertDialog.cancel()
        }
    }

    override fun onRefresh() {
        binding.swipeRefresh.isRefreshing = true
        storyAdapter.refresh()
        Timer().schedule(2000) {
            binding.swipeRefresh.isRefreshing = false
            binding.rvStory.smoothScrollToPosition(0)
        }
    }
}