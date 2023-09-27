package com.example.ceritakita.view.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.ceritakita.data.StoryRepository
import com.example.ceritakita.data.pref.UserModel

class RoutingViewModel(private val repository: StoryRepository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession()
    }
}