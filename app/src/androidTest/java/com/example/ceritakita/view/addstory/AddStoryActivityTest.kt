package com.example.ceritakita.view.addstory

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.ceritakita.JsonConverter
import com.example.ceritakita.R
import com.example.ceritakita.data.remote.retrofit.ApiConfig
import com.example.ceritakita.utils.EspressoIdlingResource
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AddStoryActivityTest{
    private val mockWebServer = MockWebServer()

    @get:Rule
    val activity = ActivityScenarioRule(AddStoryActivity::class.java)

    @Before
    fun setUp() {
        mockWebServer.start(8080)
        ApiConfig.BASE_URL = "http://127.0.0.1:8080/"
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun createStory_Success() {
        onView(withId(R.id.uploadButton)).perform(ViewActions.click())

        val mockResponse = MockResponse()
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        onView(withId(R.id.progressAddBarStory))
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))
    }

    @Test
    fun createStory_Error() {
        onView(withId(R.id.uploadButton)).perform(ViewActions.click())

        val mockResponse = MockResponse()
            .setResponseCode(401)
            .setBody(JsonConverter.readStringFromFile("failed_response.json"))
        mockWebServer.enqueue(mockResponse)

        onView(withId(R.id.progressAddBarStory))
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))
        onView(withId(R.id.descEditTextLayout))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        onView(withId(R.id.uploadButton))
            .check(ViewAssertions.matches(ViewMatchers.isNotEnabled()))
    }
}