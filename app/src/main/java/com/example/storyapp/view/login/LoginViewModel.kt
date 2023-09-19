package com.example.storyapp.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.pref.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: StoryRepository) : ViewModel() {
    fun login(email: String, password: String) = repository.getLogin(email, password)

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}