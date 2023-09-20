package com.example.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.data.remote.response.story.ListStoryItem
import com.example.storyapp.data.remote.response.story.StoryResponse
import kotlinx.coroutines.launch

class MainViewModel(private val repository: StoryRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession()
    }

    fun getStories() = repository.getStories()

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}