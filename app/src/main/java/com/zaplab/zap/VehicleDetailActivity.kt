package com.zaplab.zap

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_vehicle_detail.*

/**
 * Created by Ramshad on 4/21/18.
 */
class VehicleDetailActivity: AppCompatActivity() {

    var dbRef = FirebaseDatabase.getInstance().reference
    lateinit var userName: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_detail)

        var vehicle: Vehicle = intent.getSerializableExtra("vehicle") as Vehicle

        var imagePagerAdapter = VehicleDetailPagerAdapter(this, vehicle.imageList)
        viewPagerVehicleDetail.adapter = imagePagerAdapter
        indicatorVehicleDetail.setViewPager(viewPagerVehicleDetail)
        imagePagerAdapter.registerDataSetObserver(indicatorVehicleDetail.dataSetObserver)

        setSupportActionBar(toolbarDetailActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        collapsingToolbarVehicleDetail.title = "${vehicle.make} - ${vehicle.model}"
        collapsingToolbarVehicleDetail.setCollapsedTitleTextColor(ContextCompat.getColor(this, android.R.color.white))
        collapsingToolbarVehicleDetail.setExpandedTitleTextColor(ColorStateList.valueOf(0xffffff))

        fabVehicleDetailMessage.setOnClickListener({
            var intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("TO_USER", vehicle.ownerId)
            startActivity(intent)
        })


        // set user name
        dbRef.child("Users").child(vehicle.ownerId).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {}

            override fun onDataChange(snap: DataSnapshot?) {
                userName = snap?.child("userName")?.value.toString()
                var imageUri = snap?.child("photoUrl")?.value.toString()
                tvVehicleDetailUserName.text = userName

                Picasso.with(applicationContext)
                        .load(imageUri)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(ivVehicleDetailProfile, object : Callback {
                            override fun onSuccess() {}

                            override fun onError() {
                                // Try again online if cache failed
                                Picasso.with(applicationContext)
                                        .load(imageUri)
                                        .into(ivVehicleDetailProfile, object : Callback {
                                            override fun onSuccess() {}

                                            override fun onError() {}
                                        })
                            }
                        })
            }
        })




        tvVehicleDetailDesc.text = "\"${vehicle.desc} \""
        tvVehicleDetailTrans.text = vehicle.transmission.toString()
        tvVehicleDetailMileage.text = vehicle.odometer.toString()
        tvVehicleDetailBond.text = "$${vehicle.bond}"
        tvVehicleDetailRent.text = "$${vehicle.rent}"

    }
}