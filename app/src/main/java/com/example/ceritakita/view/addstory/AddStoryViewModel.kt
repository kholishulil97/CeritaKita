package com.example.ceritakita.view.addstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ceritakita.data.StoryRepository
import com.example.ceritakita.data.pref.UserModel
import java.io.File

class AddStoryViewModel(private val repository: StoryRepository) : ViewModel() {
    val isLocationPicked = MutableLiveData(false) // init for location new story not selected
    val coordinateLatitude = MutableLiveData(0.0)
    val coordinateLongitude = MutableLiveData(0.0)

    fun uploadImage(
        token: String,
        file: File,
        description: String,
        withLocation: Boolean = false,
        lat: String? = null,
        lon: String? = null
    ) = repository.uploadImage(token, file, description, withLocation, lat, lon)

    fun getSession(): LiveData<UserModel> {
        return repository.getSession()
    }
}