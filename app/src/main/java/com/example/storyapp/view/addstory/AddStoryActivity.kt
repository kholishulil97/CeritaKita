package com.example.storyapp.view.addstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.example.storyapp.R
import com.example.storyapp.data.Result
import com.example.storyapp.databinding.ActivityAddStoryBinding
import com.example.storyapp.utils.reduceFileImage
import com.example.storyapp.utils.uriToFile
import com.example.storyapp.view.ViewModelFactory
import com.example.storyapp.view.camera.CameraActivity
import com.example.storyapp.view.camera.CameraActivity.Companion.CAMERAX_RESULT

class AddStoryActivity : AppCompatActivity() {
    private val viewModel: AddStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityAddStoryBinding
    private var token = ""
    private var currentImageUri: Uri? = null

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.message_permission_failed),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupView()
        setMyButtonEnable()
        setupViewListener()
        setupAction()
        setupPermission()
    }

    private fun setupView() {
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupAction() {
        binding.cameraXButton.setOnClickListener { startCameraX() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.uploadButton.setOnClickListener {
            viewModel.getSession().observe(this) {
                token = it.token
                uploadImage(token)
            }
        }
    }

    private fun setupPermission() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
        setMyButtonEnable()
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun uploadImage(token: String) {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.descEditText.text.toString()

            viewModel.uploadImage(token, imageFile, description).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                            showForm(false)
                        }

                        is Result.Success -> {
                            showToast(result.data.message)
                            showLoading(false)
                            finish()
                        }

                        is Result.Error -> {
                            showToast(result.error)
                            showLoading(false)
                            showForm(true)
                        }
                    }
                }
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun showForm(state: Boolean) {
        binding.uploadButton.isEnabled = state
        binding.cameraXButton.isEnabled = state
        binding.galleryButton.isEnabled = state
    }

    private fun showLoading(state: Boolean) {
        binding.progressAddBarStory.isVisible = state
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setMyButtonEnable() {
        val resultDesc = binding.descEditText.text

        binding.uploadButton.isEnabled = (
                resultDesc.toString().isNotEmpty()
                        && currentImageUri != null
                )
    }

    private fun setupViewListener() {
        binding.descEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isEmpty()) {
                    binding.descEditTextLayout.error = getString(R.string.message_error_description)
                } else {
                    binding.descEditTextLayout.error = null
                }
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {
            }
        })
    }
}