package com.example.ceritakita.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.ceritakita.R
import com.example.ceritakita.data.Result
import com.example.ceritakita.data.remote.response.signup.SignupResponse
import com.example.ceritakita.databinding.ActivitySignupBinding
import com.example.ceritakita.view.ViewModelFactory
import com.example.ceritakita.view.login.LoginActivity

class SignupActivity : AppCompatActivity() {
    private val viewModel by viewModels<SignupViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivitySignupBinding
    private var isNameValid: Boolean = false
    private var isEmailValid: Boolean = false
    private var isPasswordValid: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setMyButtonEnable()

        setupView()
        setupViewListener()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupViewListener() {
        binding.nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                isNameValid = binding.nameEditText.error.isNullOrEmpty()
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {
            }
        })

        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                isEmailValid = binding.emailEditText.error.isNullOrEmpty()
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {
            }
        })

        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                isPasswordValid = binding.passwordEditText.error.isNullOrEmpty()
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    private fun setMyButtonEnable() {
        binding.signupButton.isEnabled = (
                isNameValid
                        && isPasswordValid
                        && isEmailValid
                )
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            viewModel.signUp(name, email, password).observe(this) { result ->
                if (result != null) {
                    when(result) {
                        is Result.Loading -> {
                            showLoading(true)
                            showForm(false)
                        }
                        is Result.Success -> {
                            processSignup(result.data)
                            showLoading(false)
                        }
                        is Result.Error -> {
                            showLoading(false)
                            displayFailedState(result.error)
                            showForm(true)
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun processSignup(data: SignupResponse) {
        if (data.error) {
            displayFailedState(data.message)
            showForm(true)
        } else {
            showSuccessDialog()
        }
    }

    private fun showSuccessDialog() {
        val mBuilder = AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.title_dialog_signup_success))
            setMessage(getString(R.string.message_dialog_signup_success))
            setPositiveButton(getString(R.string.positive_button_dialog_success), null)
        }
        val mAlertDialog = mBuilder.create()
        mAlertDialog.show()
        val mPositiveButton = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        mPositiveButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun showLoading(state: Boolean) {
        binding.progressBarSignup.isVisible = state
    }

    private fun displayFailedState(error: String) {
        binding.progressBarSignup.isVisible = false

        val mBuilder = AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.title_dialog_signup_failed))
            setMessage(getString(R.string.message_dialog_server_response) + error)
            setPositiveButton(getString(R.string.positive_button_dialog_failed), null)
        }
        val mAlertDialog = mBuilder.create()
        mAlertDialog.show()
        val mPositiveButton = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        mPositiveButton.setOnClickListener {
            mAlertDialog.cancel()
        }
    }

    private fun showForm(state: Boolean) {
        binding.nameEditTextLayout.isEnabled = state
        binding.emailEditTextLayout.isEnabled = state
        binding.passwordEditTextLayout.isEnabled = state
        binding.signupButton.isEnabled = state
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val user = ObjectAnimator.ofFloat(binding.usernameTextView, View.ALPHA, 1f).setDuration(100)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)


        AnimatorSet().apply {
            playSequentially(
                user,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 100
        }.start()
    }
}