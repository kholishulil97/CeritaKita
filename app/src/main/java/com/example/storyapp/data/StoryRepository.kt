package com.example.storyapp.data

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
import com.example.storyapp.data.remote.retrofit.ApiService
import com.example.storyapp.utils.AppExecutors
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.util.ArrayList

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

    private val _signupResponse = MutableLiveData<SignupResponse>()
    private val signupResponse: LiveData<SignupResponse> = _signupResponse

    private val resultSignup = MediatorLiveData<Result<SignupResponse>>()

    fun postSignup(name: String, email: String, password: String): LiveData<Result<SignupResponse>> {
        resultSignup.value = Result.Loading
        val client = apiService.postSignup(name, email, password)
        client.enqueue(object : Callback<SignupResponse> {
            override fun onResponse(
                call: Call<SignupResponse>,
                response: Response<SignupResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    _signupResponse.value = response.body()
                } else {
                    resultSignup.value = Result.Error(response.message().toString())
                }
            }

            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                resultSignup.value = Result.Error(t.message.toString())
            }
        })
        val localData = signupResponse
        resultSignup.addSource(localData) {
            resultSignup.value = Result.Success(it)
        }
        return resultSignup
    }

//    fun postSignup(name: String, email: String, password: String): LiveData<Result<SignupResponse>> = liveData {
//        emit(Result.Loading)
//        try {
//            val response = apiService.postSignup(name, email, password)
//            emit(Result.Success(response))
//        } catch (e: HttpException) {
//            val jsonInString = e.response()?.errorBody()?.string()
//            val errorBody = Gson().fromJson(jsonInString, LoginResponse::class.java)
//            val errorMessage = errorBody.message
//            emit(Result.Error(errorMessage))
//        }
//    }

//    fun postLogin(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
//        emit(Result.Loading)
//        try {
//            val response = apiService.postLogin(email, password)
//            emit(Result.Success(response))
//        } catch (e: HttpException) {
//            val jsonInString = e.response()?.errorBody()?.string()
//            val errorBody = Gson().fromJson(jsonInString, LoginResponse::class.java)
//            val errorMessage = errorBody.message
//            emit(Result.Error(errorMessage))
//        }
//    }
    private val _loginResponse = MutableLiveData<LoginResponse>()
    private val loginResponse: LiveData<LoginResponse> = _loginResponse

    private val resultLogin = MediatorLiveData<Result<LoginResponse>>()

    fun getLogin(email: String, password: String): LiveData<Result<LoginResponse>> {
        resultLogin.value = Result.Loading
        val client = apiService.postLogin(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    _loginResponse.value = response.body()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                resultLogin.value = Result.Error(t.message.toString())
            }
        })
        val localData = loginResponse
        resultLogin.addSource(localData) {
            resultLogin.value = Result.Success(it)
        }
        return resultLogin
    }

    private var _itemStoryResult: MutableLiveData<List<ListStoryItem>> = MutableLiveData<List<ListStoryItem>>()
    private val itemStoryResult: LiveData<List<ListStoryItem>> = _itemStoryResult

    private val result = MediatorLiveData<Result<List<ListStoryItem>>>()

    fun getStoryList(): LiveData<Result<List<ListStoryItem>>> {
        result.value = Result.Loading
        val client = apiService.getStories()
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>,
                response: Response<StoryResponse>
            ) {
                if (response.isSuccessful) {
                    val stories = response.body()?.listStory
                    val storiesList = ArrayList<ListStoryItem>()
                    appExecutors.diskIO.execute {
                        stories?.forEach { story ->
                            val storyItem = ListStoryItem(
                                story.photoUrl,
                                story.createdAt,
                                story.name,
                                story.description,
                                story.lon,
                                story.id,
                                story.lat
                            )
                            storiesList.add(storyItem)
                        }
                    }
                    _itemStoryResult.value = storiesList
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }
        })
        val localData = itemStoryResult
        result.addSource(localData) { newData: List<ListStoryItem> ->
            result.value = Result.Success(newData)
        }
        return result
    }
}