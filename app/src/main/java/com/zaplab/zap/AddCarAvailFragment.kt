package com.zaplab.zap

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
    var daysList = mutableListOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
    var availTimes = AvailableDates()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_car_avail, null)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var adapter = activity?.applicationContext?.let { CarAddAvailAdapter(it, daysList, availTimes) }
        lvAddCarAvail.adapter = adapter
    }
}