package com.zaplab.zap

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_vehicle_detail.*

/**
 * Created by Ramshad on 4/21/18.
 */
class VehicleDetailActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_detail)


        setSupportActionBar(toolbarDetailActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}