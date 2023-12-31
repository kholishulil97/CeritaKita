package com.example.ceritakita.view.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.ceritakita.view.ViewModelFactory
import com.example.ceritakita.view.main.MainActivity
import com.example.ceritakita.view.welcome.WelcomeActivity
import java.util.Timer
import kotlin.concurrent.schedule

class RoutingActivity : AppCompatActivity() {
    private val viewModel by viewModels<RoutingViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { true }

        viewModel.getSession().observe(this) {
            Timer().schedule(1500){
                routeToNextActivity(it.isLogin)
            }
        }
    }

    private fun routeToNextActivity(isLogin: Boolean) {
        if (isLogin) {
            val intent = Intent(this@RoutingActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this@RoutingActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}