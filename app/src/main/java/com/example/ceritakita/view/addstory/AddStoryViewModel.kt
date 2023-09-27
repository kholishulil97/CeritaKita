package com.example.ceritakita.view.addstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.ceritakita.data.StoryRepository
import com.example.ceritakita.data.pref.UserModel
import java.io.File

class AddStoryViewModel(private val repository: StoryRepository) : ViewModel() {
    fun uploadImage(token: String, file: File, description: String) = repository.uploadImage(token, file, description)

    fun getSession(): LiveData<UserModel> {
        return repository.getSession()
    }
}