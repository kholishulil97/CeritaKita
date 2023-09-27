package com.example.ceritakita.view.signup

import androidx.lifecycle.ViewModel
import com.example.ceritakita.data.StoryRepository

class SignupViewModel(private val repository: StoryRepository) : ViewModel() {
    fun signUp(name: String, email: String, password: String) = repository.postSignUp(name, email, password)
}