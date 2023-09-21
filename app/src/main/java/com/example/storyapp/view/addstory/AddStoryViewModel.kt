package com.example.storyapp.view.addstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.pref.UserModel
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val repository: StoryRepository) : ViewModel() {
    fun postStory(token: String, file: MultipartBody.Part, description: RequestBody) = repository.postStory(token, file, description)

    fun getSession(): LiveData<UserModel> {
        return repository.getSession()
    }
}