package com.example.ceritakita.di

import android.content.Context
import com.example.ceritakita.data.UserRepository
import com.example.ceritakita.data.pref.UserPreference
import com.example.ceritakita.data.pref.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}