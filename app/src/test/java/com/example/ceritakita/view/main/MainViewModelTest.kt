package com.example.ceritakita.view.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.ceritakita.data.StoryRepository
import com.example.ceritakita.data.entity.ListStoryItem
import com.example.ceritakita.data.paging.StoryListAdapter
import com.example.ceritakita.utils.DataDummy
import com.example.ceritakita.utils.MainDispatcherRule
import com.example.ceritakita.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    private val dummyStoriesResponse = DataDummy.generateDummyStoryEntity()
    private val dummyErrorStoriesResponse = DataDummy.generateDummyErrorStoryResponse()

    @Test
    fun `when getStory Should Not Null and Return Success`() = runTest {
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyStoriesResponse.listStory)
        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = data

        `when`(storyRepository.getStory()).thenReturn(expectedStories)

        val listStoryViewModel = MainViewModel(storyRepository)
        val actualStories: PagingData<ListStoryItem> = listStoryViewModel.story.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStories)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStoriesResponse.listStory, differ.snapshot())
        assertEquals(dummyStoriesResponse.listStory.size, differ.snapshot().size)
        assertEquals(dummyStoriesResponse.listStory[0].id, differ.snapshot()[0]?.id)
    }

    @Test
    fun `when getStory No Data Should Return No Data`() = runTest {
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyErrorStoriesResponse.listStory)
        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = data

        `when`(storyRepository.getStory()).thenReturn(expectedStories)

        val listStoryViewModel = MainViewModel(storyRepository)
        val actualStories: PagingData<ListStoryItem> = listStoryViewModel.story.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStories)

        assertNotNull(differ.snapshot())
        assertEquals(0, differ.snapshot().size)
    }

}

class StoryPagingSource : PagingSource<Int, LiveData<List<ListStoryItem>>>() {
    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int {
        return 0
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}