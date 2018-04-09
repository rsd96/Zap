package com.zaplab.zap

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_my_car.*


/**
 * Created by Ramshad on 4/8/18.
 */
class MyCarFragment: Fragment() {

    private val RES_OK = 69
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_car, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        btnAddCar.setOnClickListener({
           startActivityForResult(Intent(activity, AddCarActivity::class.java ), RES_OK)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val extras = data?.extras
        if (extras != null) {
            var position: Vehicle = extras!!.getSerializable("car_object") as Vehicle
            Log.d("MyCar", position.toString())
        }


    }
}