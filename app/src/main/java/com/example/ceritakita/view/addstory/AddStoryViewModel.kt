package com.example.ceritakita.view.addstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ceritakita.data.StoryRepository
import com.example.ceritakita.data.pref.UserModel
import com.google.android.gms.maps.model.LatLng
import java.io.File

class AddStoryViewModel(private val repository: StoryRepository) : ViewModel() {
    val isLocationPicked = MutableLiveData(false) // init for location new story not selected
    val coordinateLatitude = MutableLiveData(0.0)
    val coordinateLongitude = MutableLiveData(0.0)
    val coordinateTemp = MutableLiveData(LatLng(-2.3932797, 108.8507139)) //Indonesian area

    fun uploadImage(token: String, file: File, description: String) = repository.uploadImage(token, file, description)

    fun getSession(): LiveData<UserModel> {
        return repository.getSession()
    }
}