package com.example.storyapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.data.pref.UserPreference
import com.example.storyapp.data.remote.response.login.LoginResponse
import com.example.storyapp.data.remote.response.signup.SignupResponse
import com.example.storyapp.data.remote.response.story.ListStoryItem
import com.example.storyapp.data.remote.response.story.StoryResponse
import com.example.storyapp.data.remote.response.story.upload.UploadStoryResponse
import com.example.storyapp.data.remote.retrofit.ApiService
import com.example.storyapp.utils.AppExecutors
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.util.ArrayList
import java.util.Objects

class StoryRepository (
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    private val appExecutors: AppExecutors,
) {
    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): LiveData<UserModel> {
        return userPreference.getSession().asLiveData()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference,
            appExecutors: AppExecutors
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreference, appExecutors)
            }.also { instance = it }
    }
    fun postSignUp(name: String, email: String, password: String): LiveData<Result<SignupResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.postSignUp(name, email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.e("SignUpViewModel", "postSignUp: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun postLogin(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.postLogin(email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.e("LoginViewModel", "postLogin: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getStories(token: String): LiveData<Result<StoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStories(token)
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.d("ListStoryViewModel", "getStoriesWithLocation: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun postStory(token: String, file: MultipartBody.Part, description: RequestBody): LiveData<Result<UploadStoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.postStory(token, file, description)
            emit(Result.Success(response))
        } catch (e: Exception) {
            Log.e("CreateStoryViewModel", "postStory: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }
}