package com.example.ceritamereka.di

import android.content.Context
import com.example.ceritamereka.data.UserRepository
import com.example.ceritamereka.data.pref.UserPreference
import com.example.ceritamereka.data.pref.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}