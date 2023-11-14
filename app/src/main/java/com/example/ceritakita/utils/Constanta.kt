package com.example.ceritakita.utils

import com.google.android.gms.maps.model.LatLng

object Constanta {
    enum class LocationPicker {
        IsPicked, Latitude, Longitude
    }

    enum class PermissionRequested {
        Camera, Location
    }

    val indonesiaLocation = LatLng(-2.3932797, 108.8507139)
}