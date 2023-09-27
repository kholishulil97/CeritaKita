package com.example.ceritakita.di

import android.content.Context
import com.example.ceritakita.data.StoryRepository
import com.example.ceritakita.data.pref.UserPreference
import com.example.ceritakita.data.pref.dataStore
import com.example.ceritakita.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(apiService, pref)
    }
}