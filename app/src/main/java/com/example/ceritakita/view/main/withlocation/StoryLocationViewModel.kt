package com.example.ceritakita.view.main.withlocation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.ceritakita.data.StoryRepository
import com.example.ceritakita.data.entity.ListStoryItem
import com.example.ceritakita.data.pref.UserModel
import com.example.ceritakita.utils.Constanta
import kotlinx.coroutines.launch

class StoryLocationViewModel(private val repository: StoryRepository) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repository.getSession()
    }

    fun getStories(token: String) = repository.getStories(token)
}