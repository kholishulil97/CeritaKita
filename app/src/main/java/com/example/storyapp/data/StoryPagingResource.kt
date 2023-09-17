package com.example.storyapp.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storyapp.data.pref.UserPreference
import com.example.storyapp.data.remote.response.story.ListStoryItem
import com.example.storyapp.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.first

class StoryPagingResource(
    private val pref: UserPreference,
    private val apiService: ApiService
) : PagingSource<Int, ListStoryItem>() {

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX

            val responseData = apiService.getStories(position, params.loadSize)
            if (responseData.isSuccessful) {
                Log.d("Story Paging Source", "Load: ${responseData.body()}")
                LoadResult.Page(
                    data = responseData.body()?.listStory ?: emptyList(),
                    prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                    nextKey = if (responseData.body()?.listStory.isNullOrEmpty()) null else position + 1
                )
            } else {
                LoadResult.Error(Exception("Failed"))
            }
        } catch (e: Exception) {
            Log.d("Exception", "Load Error: ${e.message}")
            return LoadResult.Error(e)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}