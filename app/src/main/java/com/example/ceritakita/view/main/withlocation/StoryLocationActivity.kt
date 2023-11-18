package com.example.ceritakita.view.main.withlocation

import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.ceritakita.R
import com.example.ceritakita.data.entity.ListStoryItem
import com.example.ceritakita.databinding.ActivityStoryLocationBinding
import com.example.ceritakita.view.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class StoryLocationActivity : AppCompatActivity(), OnMapReadyCallback {
    private val viewModel by viewModels<StoryLocationViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var token = ""
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityStoryLocationBinding

    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoryLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
        setupUser()
        setMapStyle()
    }

    private fun setupUser() {
        viewModel.getSession().observe(this) {
            token = it.token
            if (token.isEmpty()) {
                showFailedDialog()
            } else {
                setupData()
            }
        }
    }

    private fun setupData() {
        if (token.isNotEmpty()) {
            viewModel.getStories(token).observe(this@StoryLocationActivity) { result ->
                if (result != null) {
                    when (result) {
                        is com.example.ceritakita.data.Result.Loading -> {
                            binding.progressBarStory.visibility = View.VISIBLE
                        }
                        is com.example.ceritakita.data.Result.Success -> {
                            binding.progressBarStory.visibility = View.GONE
                            val storyData = result.data.listStory
                            addManyMarker(storyData)
                        }
                        is com.example.ceritakita.data.Result.Error -> {
                            binding.progressBarStory.visibility = View.GONE
                            Toast.makeText(
                                this@StoryLocationActivity,
                                getString(R.string.message_dialog_server_error) + result.error,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }
    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun addManyMarker(storyData: List<ListStoryItem>) {
        storyData.forEach { data ->
            val latLng = LatLng(data.lat, data.lon)
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(data.name)
                    .snippet(data.description)
            )
            boundsBuilder.include(latLng)
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }

    private fun showFailedDialog() {
        val mBuilder = AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.title_dialog_token_failed))
            setMessage(getString(R.string.message_dialog_token_failed))
            setPositiveButton(getString(R.string.positive_button_dialog_failed), null)
        }
        val mAlertDialog = mBuilder.create()
        mAlertDialog.show()
        val mPositiveButton = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        mPositiveButton.setOnClickListener {
            mAlertDialog.cancel()
            finish()
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        this,
                        R.raw.map_style
                    )
                )
            if (!success) {
                Log.e("MAPS", "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e("MAPS", "Can't find style. Error: ", exception)
        }
    }
}