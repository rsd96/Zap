package com.zaplab.zap

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_my_car.*


/**
 * Created by Ramshad on 4/8/18.
 */
class MyCarFragment: Fragment() {

    private val RES_OK = 69
    var vehicleList = mutableListOf<Vehicle>()
    var dbRef = FirebaseDatabase.getInstance().reference
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_car, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        dbRef.child("Vehicles").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snap: DataSnapshot?) {
                snap?.let {
                    vehicleList.clear()
                    for (x in snap.children) {
                        if (x.child("ownerId").value.toString() == (activity?.application as Global).currentUser.uid)
                            vehicleList.add(x.getValue(Vehicle::class.java)!!)
                    }

                    if (vehicleList.isNotEmpty()) {
                        var pagerAdapter = VehicleListPagerAdapter(activity!!, vehicleList)
                        var myCarsPagerAdapter = MyCarsPagerAdapter(activity!!, vehicleList)
                        viewPagerMyCars.adapter = myCarsPagerAdapter
                        viewPagerMyCars.clipToPadding = false
                        viewPagerMyCars.setPadding(50, 0, 50, 0)
                    }
                }

            }

            override fun onCancelled(p0: DatabaseError?) {}

        })

        viewPagerMyCars.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
            }

        })


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

            FirebaseDatabase.getInstance().reference.child("Vehicle").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                }

                override fun onDataChange(snap: DataSnapshot?) {
                    for (x in snap?.children!!) {

                    }
                }

            })
        }


    }
}