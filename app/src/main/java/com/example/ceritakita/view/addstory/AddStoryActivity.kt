package com.example.ceritakita.view.addstory

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.example.ceritakita.R
import com.example.ceritakita.data.Result
import com.example.ceritakita.databinding.ActivityAddStoryBinding
import com.example.ceritakita.utils.LocationPicker
import com.example.ceritakita.utils.getAddress
import com.example.ceritakita.utils.reduceFileImage
import com.example.ceritakita.utils.uriToFile
import com.example.ceritakita.view.ViewModelFactory
import com.example.ceritakita.view.camera.CameraActivity
import com.example.ceritakita.view.camera.CameraActivity.Companion.CAMERAX_RESULT
import java.util.Locale

class AddStoryActivity : AppCompatActivity() {
    private val viewModel: AddStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityAddStoryBinding
    private var token = ""
    private var currentImageUri: Uri? = null
    private var isPicked: Boolean? = false
    private var getResult: ActivityResultLauncher<Intent>? = null
    private var getResultPermission: ActivityResultLauncher<Intent>? = null

    companion object {
        private val CAMERA_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION)
        private const val REQUEST_CODE_CAMERA_PERMISSIONS = 10
        private const val REQUEST_CODE_LOCATION_PERMISSIONS = 11
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.let { res ->
                    isPicked = res.getBooleanExtra(LocationPicker.IsPicked.name, false)
                    viewModel.isLocationPicked.postValue(isPicked)
                    val lat = res.getDoubleExtra(
                        LocationPicker.Latitude.name,
                        0.0
                    )
                    val lon = res.getDoubleExtra(
                        LocationPicker.Longitude.name,
                        0.0
                    )
                    Geocoder(this, Locale("in"))
                        .getAddress(lat, lon) { address: android.location.Address? ->
                            if (address != null) {
                                val fullAddress = address.getAddressLine(0)
                                StringBuilder("📌 ")
                                    .append(fullAddress).toString()
                                binding.fieldLocation.text = fullAddress
                            } else {
                                binding.fieldLocation.text = getString(R.string.location_unknown)
                            }
                        }
                    viewModel.coordinateLatitude.postValue(lat)
                    viewModel.coordinateLongitude.postValue(lon)
                }
            }
        }

        getResultPermission = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            setupPermission()
        }

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
        binding.btnSelectLocation.setOnClickListener {
            /* check permission to granted apps pick user location */
            if (locationPermissionsGranted()) {
                val intentPickLocation = Intent(this, AddStoryPickLocation::class.java)
                getResult?.launch(intentPickLocation)
            } else {
                ActivityCompat.requestPermissions(
                    this@AddStoryActivity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    REQUEST_CODE_LOCATION_PERMISSIONS
                )
            }
        }
        binding.btnClearLocation.setOnClickListener {
            viewModel.isLocationPicked.postValue(false)
        }
    }

    private fun setupPermission() {
        if (!cameraPermissionsGranted()) {
            if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
                } else {
                    false
                }
            ) {
                showFailedDialog("Location")
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    CAMERA_PERMISSIONS,
                    REQUEST_CODE_CAMERA_PERMISSIONS
                )
            }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSIONS) {
            if (!cameraPermissionsGranted()) {
                showFailedDialog("Camera")
            }
        } else if (requestCode == REQUEST_CODE_LOCATION_PERMISSIONS) {
            if (!locationPermissionsGranted()) {
                showFailedDialog("Location")
            }
        }
    }

    private fun cameraPermissionsGranted() = CAMERA_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun locationPermissionsGranted() = LOCATION_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun showFailedDialog(error: String) {
        val mBuilder = AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.dialog_title_permission_required))
            setMessage(getString(R.string.dialog_message_permission_failed) + error)
            setPositiveButton(getString(R.string.dialog_button_allow), null)
            setNegativeButton(getString(R.string.dialog_button_cancel), null)
            setCancelable(false)
        }
        val mAlertDialog = mBuilder.create()
        mAlertDialog.show()
        val mPositiveButton = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        mPositiveButton.setOnClickListener {
            openSettingPermission(this)
            mAlertDialog.cancel()
        }
        val mNegativeButton = mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        mNegativeButton.setOnClickListener {
            mAlertDialog.cancel()
            finish()
        }
    }

    private fun openSettingPermission(context: Context) {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.fromParts("package", context.packageName, null)
        getResultPermission?.launch(intent)
    }
}