package com.example.storyapp.view.signup

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.StoryRepository

class SignupViewModel(private val repository: StoryRepository) : ViewModel() {
    fun signUp(name: String, email: String, password: String) = repository.postSignup(name, email, password)
}