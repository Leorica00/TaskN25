package com.example.taskn25.presentation.event

import com.google.android.gms.maps.model.LatLng

sealed interface MapsEvent {
    class ChangeLocationEvent(val latLng: LatLng): MapsEvent
    class ChangeErrorMessage(val error: String): MapsEvent
}
