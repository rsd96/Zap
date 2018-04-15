package com.zaplab.zap

import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_add_car_avail.*

/**
 * Created by Ramshad on 4/11/18.
 */
class AddCarAvailFragment: Fragment() {
    var daysList = mutableListOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    var availTimes = AvailableDates()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_car_avail, null)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var adapter = activity?.applicationContext?.let { CarAddAvailAdapter(it, daysList, availTimes) }
        lvAddCarAvail.adapter = adapter
        btnAddCarAvailNext.setOnClickListener({


            if(adapter?.availableDates?.sunday?.isEmpty()!! or
                    adapter?.availableDates?.monday?.isEmpty() or
                    adapter?.availableDates?.tuesday?.isEmpty() or
                    adapter?.availableDates?.wednesday?.isEmpty() or
                    adapter?.availableDates?.thursday?.isEmpty() or
                    adapter?.availableDates?.friday?.isEmpty() or
                    adapter?.availableDates?.saturday?.isEmpty() ) {
                var dialog =  AlertDialog.Builder(activity)
                dialog.setMessage("Please provide available times for all the days.")
                        .setPositiveButton("Okay", null)
                dialog.create().show()

            } else {
                AddCarActivity.vehicle.availability = adapter?.availableDates!!
                (activity as AddCarActivity).nextPager(3)
            }

        })
    }
}