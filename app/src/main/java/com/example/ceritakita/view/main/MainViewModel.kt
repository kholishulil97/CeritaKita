package com.example.ceritakita.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.ceritakita.data.StoryRepository
import com.example.ceritakita.data.entity.ListStoryItem
import com.example.ceritakita.data.pref.UserModel
import kotlinx.coroutines.launch

class MainViewModel(private val repository: StoryRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getStory(token: String) = repository.getStory(token).cachedIn(viewModelScope)
}