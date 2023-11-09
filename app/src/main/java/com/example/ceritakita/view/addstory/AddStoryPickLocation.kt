package com.example.ceritakita.view.addstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.ceritakita.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.ceritakita.databinding.ActivityAddStoryPickLocationBinding
import com.example.ceritakita.databinding.CustomTooltipPickLocationStoryBinding
import com.example.ceritakita.utils.Constanta
import com.example.ceritakita.utils.getAddress
import com.example.ceritakita.view.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import java.util.Locale

class AddStoryPickLocation : AppCompatActivity(), OnMapReadyCallback, GoogleMap.InfoWindowAdapter {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityAddStoryPickLocationBinding
    private val viewModel: AddStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryPickLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.btnCancel.setOnClickListener {
            viewModel.isLocationPicked.postValue(false)
            finish()
        }
        binding.btnSelectLocation.setOnClickListener {
            /* check is location picked before next step */
            if (viewModel.isLocationPicked.value == true) {
                val intent = Intent()
                intent.putExtra(
                    Constanta.LocationPicker.IsPicked.name,
                    viewModel.isLocationPicked.value
                )
                intent.putExtra(
                    Constanta.LocationPicker.Latitude.name,
                    viewModel.coordinateLatitude.value
                )
                intent.putExtra(
                    Constanta.LocationPicker.Longitude.name,
                    viewModel.coordinateLongitude.value
                )
                setResult(RESULT_OK, intent)
                finish()
            } else {
                showFailedDialog()
            }
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isTiltGesturesEnabled = true
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constanta.indonesiaLocation, 4f))
        mMap.setInfoWindowAdapter(this)
        mMap.setOnInfoWindowClickListener { marker ->
            postLocationSelected(marker.position.latitude, marker.position.longitude)
            marker.hideInfoWindow()
        }
        mMap.setOnMapClickListener {
            mMap.clear()
            mMap.addMarker(
                MarkerOptions()
                    .position(
                        LatLng(
                            it.latitude,
                            it.longitude
                        )
                    )
            )?.showInfoWindow()
        }
        mMap.setOnPoiClickListener {
            Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
            mMap.clear()
            mMap.addMarker(
                MarkerOptions()
                    .position(
                        LatLng(
                            it.latLng.latitude,
                            it.latLng.longitude
                        )
                    )
            )?.showInfoWindow()
        }
        setMapStyle()
        getMyLastLocation()
    }

    private fun postLocationSelected(lat: Double, lon: Double) {
        Geocoder(this, Locale("in"))
            .getAddress(lat, lon) { address: android.location.Address? ->
                if (address != null) {
                    binding.addressBar.text = address.getAddressLine(0)
                } else {
                    binding.addressBar.text = getString(R.string.location_unknown)
                }
            }
        viewModel.isLocationPicked.postValue(true)
        viewModel.coordinateLatitude.postValue(lat)
        viewModel.coordinateLongitude.postValue(lon)
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

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLastLocation()
                }
                else -> {
                    // No location access granted.
                }
            }
        }

    /* check permission in this activity -> related to fusedLocation requirements*/
    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                /* if user location fetched -> add marker & trigger input to user location */
                location?.let {
                    mMap.addMarker(
                        MarkerOptions().position(LatLng(it.latitude, it.longitude))
                    )
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 20f)
                    )
                    postLocationSelected(it.latitude, it.longitude)
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    override fun getInfoContents(p0: Marker): View? {
        return null
    }

    override fun getInfoWindow(p0: Marker): View? {
        val bindingTooltips =
            CustomTooltipPickLocationStoryBinding.inflate(LayoutInflater.from(this))
        Geocoder(this, Locale.getDefault())
            .getAddress(p0.position.latitude, p0.position.longitude) { address: android.location.Address? ->
                if (address != null) {
                    bindingTooltips.location.text = address.getAddressLine(0)
                } else {
                    bindingTooltips.location.text = getString(R.string.location_unknown)
                }
            }
        return bindingTooltips.root
    }

    private fun showFailedDialog() {
        val mBuilder = AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.title_dialog_signup_failed))
            setMessage(getString(R.string.message_dialog_server_response))
            setPositiveButton(getString(R.string.positive_button_dialog_failed), null)
        }
        val mAlertDialog = mBuilder.create()
        mAlertDialog.show()
        val mPositiveButton = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        mPositiveButton.setOnClickListener {
            mAlertDialog.cancel()
        }
    }
}