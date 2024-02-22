package com.example.taskn25.presentation.screen.maps

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.taskn22.presentation.base.BaseFragment
import com.example.taskn25.R
import com.example.taskn25.databinding.FragmentMapsBinding
import com.example.taskn25.presentation.event.MapsEvent
import com.example.taskn25.presentation.extension.showSnackBar
import com.example.taskn25.presentation.lisener.maps.MapsSearchSelectPlaceListener
import com.example.taskn25.presentation.screen.maps.bottomsheet.MapsSearchBottomSheetFragment
import com.example.taskn25.presentation.state.MapsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

class MapsFragment : BaseFragment<FragmentMapsBinding>(FragmentMapsBinding::inflate),
    MapsSearchSelectPlaceListener {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var map: GoogleMap
    private val viewModel: MapsViewModel by viewModels()

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        updateLocationUi(LatLng(41.7934135, 44.8025545))
    }


    override fun setUp() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun setUpListeners() {
        binding.btnGetCurrentLocation.setOnClickListener {
            updateCurrentLocationUI()
        }

        binding.btnGetSeachBottomSheet.setOnClickListener {
            setUpBottomSheet()
        }
    }

    override fun setUpObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.mapStateFlow.collect {
                    handleState(it)
                }
            }
        }
    }

    private fun handleState(state: MapsState) {
        with(state) {
            mapLocation?.let {
                updateLocationUi(it)
            }

            errorMessage?.let {
                requireView().showSnackBar(it)
            }
        }
    }

    private fun updateCurrentLocationUI() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    if (task.result != null) {
                        viewModel.onEvent(
                            MapsEvent.ChangeLocationEvent(
                                LatLng(
                                    task.result.latitude,
                                    task.result.longitude
                                )
                            )
                        )
                    }
                } else {
                    map.uiSettings.isMyLocationButtonEnabled = false
                    viewModel.onEvent(MapsEvent.ChangeErrorMessage(task.exception?.message.toString()))
                }
            }
        }
    }

    private fun setUpBottomSheet() {
        MapsSearchBottomSheetFragment().also {
            it.setBottomSheetListener(this@MapsFragment)
            it.show(parentFragmentManager, MapsSearchBottomSheetFragment.BOTTOM_SHEET)
        }
    }

    private fun updateLocationUi(latLng: LatLng) = with(map) {
        clear()
        moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f))
        addMarker(MarkerOptions().position(latLng))
    }


    override fun transferToNewLocation(latLng: LatLng) {
        viewModel.onEvent(MapsEvent.ChangeLocationEvent(latLng))
    }
}