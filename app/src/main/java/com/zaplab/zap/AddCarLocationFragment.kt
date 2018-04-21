package com.zaplab.zap

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import kotlinx.android.synthetic.main.fragment_add_car_location.*


/**
 * Created by Ramshad on 4/15/18.
 */
class AddCarLocationFragment: Fragment() {

    private var PLACE_AUTOCOMPLETE_REQUEST_CODE = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_car_location, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        btnAddCarLocation.setOnClickListener({
            try {
                val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                        .build(activity)
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE)
            } catch (e: GooglePlayServicesRepairableException) {

            } catch (e: GooglePlayServicesNotAvailableException) {

            }
        })

        btnAddCarLocationNext.setOnClickListener( {
            (activity as AddCarActivity).nextPager(4)
        })



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val place = PlaceAutocomplete.getPlace(activity, data)
                tvAddCarLocation.visibility = View.VISIBLE
                tvAddCarLocation.text = "${place.name}\n${place.address}"
                var geocoder = Geocoder(activity)
                var lat = place.latLng.latitude
                var long = place.latLng.longitude
                var addresses = geocoder.getFromLocation(lat, long, 1)
                val address = addresses[0].getAddressLine(0)
                val city = addresses[0].locality
                var country = addresses[0].countryName
                (activity as AddCarActivity).vehicle.city = city
                (activity as AddCarActivity).vehicle.country = country
                (activity as AddCarActivity).vehicle.lat = lat
                (activity as AddCarActivity).vehicle.long = long
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                val status = PlaceAutocomplete.getStatus(activity, data)
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }
}