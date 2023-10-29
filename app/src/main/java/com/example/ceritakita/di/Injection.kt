package com.example.ceritakita.di

import android.content.Context
import com.example.ceritakita.data.StoryRepository
import com.example.ceritakita.data.database.StoryDatabase
import com.example.ceritakita.data.pref.UserPreference
import com.example.ceritakita.data.pref.dataStore
import com.example.ceritakita.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        val database = StoryDatabase.getDatabase(context)
        return StoryRepository.getInstance(apiService, pref, database)
    }
}