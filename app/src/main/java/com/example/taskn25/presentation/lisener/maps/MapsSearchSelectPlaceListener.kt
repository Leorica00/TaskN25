package com.example.taskn25.presentation.lisener.maps

import com.google.android.gms.maps.model.LatLng

interface MapsSearchSelectPlaceListener {
    fun transferToNewLocation(latLng: LatLng)
}