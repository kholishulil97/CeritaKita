package com.example.ceritakita.data.remote.response.story

import com.example.ceritakita.data.entity.ListStoryItem
import com.google.gson.annotations.SerializedName

data class StoryResponse(

	@field:SerializedName("listStory")
	val listStory: List<ListStoryItem>,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)