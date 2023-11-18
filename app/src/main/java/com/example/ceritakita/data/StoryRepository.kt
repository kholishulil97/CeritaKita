package com.example.ceritakita.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.ceritakita.data.database.StoryDatabase
import com.example.ceritakita.data.entity.ListStoryItem
import com.example.ceritakita.data.paging.StoryRemoteMediator
import com.example.ceritakita.data.pref.UserModel
import com.example.ceritakita.data.pref.UserPreference
import com.example.ceritakita.data.remote.response.login.LoginResponse
import com.example.ceritakita.data.remote.response.signup.SignupResponse
import com.example.ceritakita.data.remote.response.story.StoryResponse
import com.example.ceritakita.data.remote.response.story.upload.UploadStoryResponse
import com.example.ceritakita.data.remote.retrofit.ApiService
import com.example.ceritakita.utils.wrapEspressoIdlingResource
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
    private val storyDatabase: StoryDatabase
) {
    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference,
            storyDatabase: StoryDatabase
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreference, storyDatabase)
            }.also { instance = it }
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): LiveData<UserModel> {
        return userPreference.getSession().asLiveData()
    }

    suspend fun logout() {
        userPreference.logout()
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
        wrapEspressoIdlingResource {
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

    fun uploadImage(
        token: String,
        imageFile: File,
        description: String,
        withLocation: Boolean = false,
        lat: String? = null,
        lon: String? = null
    ) = liveData {
        emit(Result.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        if (withLocation) {
            val positionLat = lat?.toRequestBody("text/plain".toMediaType())
            val positionLon = lon?.toRequestBody("text/plain".toMediaType())
            wrapEspressoIdlingResource {
                try {
                    val successResponse = apiService.uploadImage(
                        token,
                        multipartBody,
                        requestBody,
                        positionLat!!,
                        positionLon!!)
                    emit(Result.Success(successResponse))
                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, UploadStoryResponse::class.java)
                    emit(Result.Error(errorResponse.message))
                }
            }
        } else {
            wrapEspressoIdlingResource {
                try {
                    val successResponse = apiService.uploadImage(
                        token,
                        multipartBody,
                        requestBody)
                    emit(Result.Success(successResponse))
                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, UploadStoryResponse::class.java)
                    emit(Result.Error(errorResponse.message))
                }
            }
        }
    }

    fun getStory(token: String): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(token, storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }
}