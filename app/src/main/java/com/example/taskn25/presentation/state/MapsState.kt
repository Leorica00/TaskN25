package com.example.taskn25.presentation.state

import com.google.android.gms.maps.model.LatLng

data class MapsState(
    val mapLocation: LatLng? = null,
    val errorMessage: String? = null
)
