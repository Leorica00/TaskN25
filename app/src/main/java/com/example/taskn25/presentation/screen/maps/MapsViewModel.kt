package com.example.taskn25.presentation.screen.maps

import androidx.lifecycle.ViewModel
import com.example.taskn25.presentation.event.MapsEvent
import com.example.taskn25.presentation.state.MapsState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MapsViewModel: ViewModel() {
    private val _mapStateFlow = MutableStateFlow(MapsState())
    val mapStateFlow = _mapStateFlow.asStateFlow()

    fun onEvent(event: MapsEvent) {
        when(event) {
            is MapsEvent.ChangeLocationEvent -> updateLocation(latLng = event.latLng)
            is MapsEvent.ChangeErrorMessage -> updateErrorMessage(message = event.error)
        }
    }

    private fun updateLocation(latLng: LatLng) {
        _mapStateFlow.update { currentState -> currentState.copy(mapLocation = latLng, errorMessage = null) }
    }

    private fun updateErrorMessage(message: String) {
        _mapStateFlow.update { currentState -> currentState.copy(errorMessage = message) }
    }
}