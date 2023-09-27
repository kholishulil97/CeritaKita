package com.example.ceritakita.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ceritakita.data.StoryRepository
import com.example.ceritakita.data.pref.UserModel
import kotlinx.coroutines.launch

class MainViewModel(private val repository: StoryRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession()
    }

    fun getStories(token: String) = repository.getStories(token)

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}