package com.example.storyapp.data.remote.response.story.upload

import com.google.gson.annotations.SerializedName

data class UploadStoryResponse (
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String
)