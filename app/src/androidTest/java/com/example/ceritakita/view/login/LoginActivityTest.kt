package com.example.ceritakita.view.login

import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.isNotEnabled
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.ceritakita.JsonConverter
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
class LoginActivityTest {

    private val mockWebServer = MockWebServer()

    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)

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
    fun doLogin_Success() {
        onView(withId(com.example.ceritakita.R.id.loginButton)).perform(click())

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("login_success_response.json"))
        mockWebServer.enqueue(mockResponse)

        onView(withId(com.example.ceritakita.R.id.progressBarLogin))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))
    }

    @Test
    fun doLogin_Error() {
        onView(withId(com.example.ceritakita.R.id.loginButton)).perform(click())

        val mockResponse = MockResponse()
            .setResponseCode(401)
        mockWebServer.enqueue(mockResponse)

        onView(withId(com.example.ceritakita.R.id.progressBarLogin))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))
        onView(withId(com.example.ceritakita.R.id.emailEditTextLayout))
            .check(matches(isEnabled()))
        onView(withId(com.example.ceritakita.R.id.passwordEditTextLayout))
            .check(matches(isEnabled()))
        onView(withId(com.example.ceritakita.R.id.loginButton))
            .check(matches(isNotEnabled()))
    }
}