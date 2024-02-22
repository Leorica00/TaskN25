package com.example.taskn25.presentation.screen.maps.bottomsheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.taskn25.R
import com.example.taskn25.databinding.MapsSearchBottomsheetBinding
import com.example.taskn25.presentation.lisener.maps.MapsSearchSelectPlaceListener
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MapsSearchBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: MapsSearchBottomsheetBinding
    private var listener: MapsSearchSelectPlaceListener? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MapsSearchBottomsheetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSearchView()

        bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
    }

    fun setBottomSheetListener(listener: MapsSearchSelectPlaceListener) {
        this.listener = listener
    }

    private fun setUpSearchView() {
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        autocompleteFragment.setCountries("GE")

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                place.latLng?.let { listener?.transferToNewLocation(it) }
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }

            override fun onError(status: Status) {
                Log.i("error occured", "An error occurred: $status ${status.statusMessage}")
            }
        })
    }


    companion object {
        const val BOTTOM_SHEET = "BottomSheet"
    }
}