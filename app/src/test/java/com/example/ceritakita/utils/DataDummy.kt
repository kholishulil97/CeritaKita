package com.example.ceritakita.utils

import com.example.ceritakita.data.entity.ListStoryItem
import com.example.ceritakita.data.remote.response.story.StoryResponse

object DataDummy {
    fun generateDummyStoryEntity(): StoryResponse {
        val storyList = ArrayList<ListStoryItem>()
        for (i in 0..10) {
            val story = ListStoryItem(
                "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                "2022-01-08T06:34:18.598Z",
                "Dimas",
                "Lorem Ipsum",
                -16.002,
                "story-FvU4u0Vp2S3PMsFg",
                -10.212
            )
            storyList.add(story)
        }
        return StoryResponse(
            error = false,
            message = "Stories fetched successfully",
            listStory = storyList
        )
    }
}

