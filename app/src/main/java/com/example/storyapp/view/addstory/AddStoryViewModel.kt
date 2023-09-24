package com.example.storyapp.view.addstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.pref.UserModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class AddStoryViewModel(private val repository: StoryRepository) : ViewModel() {
    fun uploadImage(token: String, file: File, description: String) = repository.uploadImage(token, file, description)

    fun getSession(): LiveData<UserModel> {
        return repository.getSession()
    }
}