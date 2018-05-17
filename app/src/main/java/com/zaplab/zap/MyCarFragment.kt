package com.zaplab.zap

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_my_car.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Ramshad on 4/8/18.
 */
class MyCarFragment: Fragment() {

    private val RES_OK = 69
    var vehicleList = mutableListOf<Vehicle>()
    var vehicleIdList = mutableListOf<String>()
    var dbRef = FirebaseDatabase.getInstance().reference
    val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")


    lateinit var myCarsPagerAdapter: MyCarsPagerAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_car, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        getMyCars()

        viewPagerMyCars.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                setVehicleStatus(position)
            }

        })

        // Add new vehicle
        btnMyCarsAddCar.setOnClickListener({
            var intent = Intent(activity, AddCarActivity::class.java)
            intent.putExtra("EDIT_MODE", false)
           startActivityForResult(intent, RES_OK)
        })


        // Remove vehicle
        btnMyCarsRemove.setOnClickListener({
            var alertDialog = AlertDialog.Builder(activity)
            alertDialog.setMessage("Are you sure you want to remove this vehicle ?")
            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                removeCar()
            })
            alertDialog.setNegativeButton("Cancel", null)
            alertDialog.show()

        })


        // Edit vehicle info
        btnMyCarsEdit.setOnClickListener({
            editCar(viewPagerMyCars.currentItem)
        })
    }

    private fun setVehicleStatus(position: Int) {
        dbRef.child("Bookings").child(vehicleIdList[position]).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
            }
            override fun onDataChange(snap: DataSnapshot?) {
                if ( snap != null && snap.value != null) {
                    for ( x in snap.children) {
                        var toDate = dateTimeFormat.parse(x.child("to").value.toString())
                        if (toDate.after(Date())) {
                            tvMyCarsStatus.text = "Booked"
                            break
                        }
                    }

                    if (tvMyCarsStatus.text != "Booked") {
                        tvMyCarsStatus.text = "Listed"
                        btnMyCarsEdit.visibility = View.VISIBLE
                        btnMyCarsRemove.visibility = View.VISIBLE
                    } else {
                        btnMyCarsEdit.visibility = View.GONE
                        btnMyCarsRemove.visibility = View.GONE
                    }
                } else {
                    tvMyCarsStatus.text = "Listed"
                    btnMyCarsEdit.visibility = View.VISIBLE
                    btnMyCarsRemove.visibility = View.VISIBLE
                }
            }

        })
    }

    /**
     * Get all cars in database that belongs to current user
     */
    private fun getMyCars() {
        dbRef.child("Vehicles").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snap: DataSnapshot?) {
                snap?.let {
                    vehicleList.clear()
                    for (x in snap.children) {
                        if (x.child("ownerId").value.toString() == (activity?.application as Global).currentUser.uid) {
                            vehicleIdList.add(x.key)
                            vehicleList.add(x.getValue(Vehicle::class.java)!!)
                        }
                    }

                    if (vehicleList.isNotEmpty()) {
                        btnMyCarsEdit?.visibility = View.VISIBLE
                        btnMyCarsRemove?.visibility = View.VISIBLE
                        ivMyCarsStatus?.visibility = View.VISIBLE
                        tvMyCarsStatus?.visibility = View.VISIBLE
                        setVehicleStatus(0)
                    } else {
                        btnMyCarsEdit?.visibility = View.GONE
                        btnMyCarsRemove?.visibility = View.GONE
                        ivMyCarsStatus?.visibility = View.GONE
                        tvMyCarsStatus?.visibility = View.GONE
                    }

                    myCarsPagerAdapter = MyCarsPagerAdapter(activity!!, vehicleList)
                    viewPagerMyCars.adapter = myCarsPagerAdapter
                    viewPagerMyCars.clipToPadding = false
                    viewPagerMyCars.setPadding(50, 0, 50, 0)
                }

            }

            override fun onCancelled(p0: DatabaseError?) {}

        })
    }


    /**
     * Edit current vehicle with new info
     */
    private fun editCar(position: Int) {
        Log.d("MY_CAR", position.toString())
        var intent = Intent(activity, AddCarActivity::class.java)
        intent.putExtra("EDIT_MODE", true)
        intent.putExtra("EDIT_VEHICLE", vehicleList[position])
        intent.putExtra("EDIT_VEHICLE_ID", vehicleIdList[position])
        startActivityForResult(intent, RES_OK)
    }

    /**
     * Remove the current vehicle
     */
    private fun removeCar() {
        dbRef.child("Vehicles").child(vehicleIdList[viewPagerMyCars.currentItem]).removeValue().addOnCompleteListener {
            Toast.makeText(activity, "Vehicle successfully removed!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val extras = data?.extras
        if (extras != null) {
            var position: Vehicle = extras.getSerializable("car_object") as Vehicle
            Log.d("MyCar", position.toString())
        }


    }
}