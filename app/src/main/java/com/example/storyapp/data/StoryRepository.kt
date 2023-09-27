package com.example.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.example.storyapp.data.pref.UserModel
import com.example.storyapp.data.pref.UserPreference
import com.example.storyapp.data.remote.response.login.LoginResponse
import com.example.storyapp.data.remote.response.signup.SignupResponse
import com.example.storyapp.data.remote.response.story.StoryResponse
import com.example.storyapp.data.remote.response.story.upload.UploadStoryResponse
import com.example.storyapp.data.remote.retrofit.ApiService
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoryRepository (
    private val apiService: ApiService,
    private val userPreference: UserPreference,
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
            userPreference: UserPreference
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreference)
            }.also { instance = it }
    }
    fun postSignUp(name: String, email: String, password: String): LiveData<Result<SignupResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.postSignUp(name, email, password)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, SignupResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage))
        }
    }

    fun postLogin(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.postLogin(email, password)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, SignupResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage))
        }
    }

    fun getStories(token: String): LiveData<Result<StoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStories(token)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, SignupResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error(errorMessage))
        }
    }

    fun uploadImage(token: String, imageFile: File, description: String) = liveData {
        emit(Result.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val successResponse = apiService.uploadImage(token, multipartBody, requestBody)
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, UploadStoryResponse::class.java)
            emit(Result.Error(errorResponse.message))
        }

    }
}