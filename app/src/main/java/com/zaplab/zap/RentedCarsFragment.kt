package com.zaplab.zap

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_rented_car.*

/**
 * Created by Ramshad on 4/8/18.
 */
class RentedCarsFragment: Fragment() {

    var vehicleList = mutableListOf<Vehicle>()
    var vehicleIdList = mutableListOf<String>()
    var dbRef = FirebaseDatabase.getInstance().reference
    lateinit var rentedCarsPagerAdapter: MyCarsPagerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rented_car, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        getRentedCars()

        viewPagerRentedCars.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                loadOwnerInfo(position)
            }

            override fun onPageSelected(position: Int) {

            }

        })

    }

    /**
     * Load name and profile pic of vehicle owner of the rented car
     */
    private fun loadOwnerInfo(position: Int) {
        dbRef.child("Users").child(vehicleList[position].ownerId).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {}

            override fun onDataChange(snap: DataSnapshot?) {
                if ( snap != null) {

                    

                    var imageUri = snap.child("photoUrl").value.toString()
                    if (imageUri != "null" && ivRentedCarsProfile != null) {
                        Picasso.with(context)
                                .load(imageUri)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .into(ivRentedCarsProfile, object : Callback {
                                    override fun onSuccess() {}

                                    override fun onError() {
                                        // Try again online if cache failed
                                        Picasso.with(context)
                                                .load(imageUri)
                                                .into(ivRentedCarsProfile, object : Callback {
                                                    override fun onSuccess() {}

                                                    override fun onError() {}
                                                })
                                    }
                                })
                    }

                    if (tvRentedCarsName != null)
                        tvRentedCarsName.text = snap.child("userName").value.toString()

                    btnRentedCarsChat?.setOnClickListener {
                        chatWithOwner(snap.key.toString())
                    }
                }
            }

        })
    }

    private fun chatWithOwner(ownerId: String) {
        var intent = Intent(activity, ChatActivity::class.java)
        // Pass id of vehicle owner
        intent.putExtra("TO_USER", ownerId)
        startActivity(intent)
    }

    /**
     * Get all cars in database that belongs to current user
     */
    private fun getRentedCars() {

        dbRef.child("Transactions").addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {
                    }

                    override fun onDataChange(snap: DataSnapshot?) {
                        if ( snap != null) {
                            vehicleIdList.clear()
                            for ( x in snap.children) {
                                if (x.child("renter").value?.toString() == (activity?.application as Global).currentUser.uid) {
                                    vehicleIdList.add(x.child("vehicleId").value.toString())
                                }
                            }
                        }

                        dbRef.child("Vehicles").addValueEventListener(object: ValueEventListener {
                            override fun onDataChange(snap: DataSnapshot?) {
                                snap?.let {
                                    vehicleList.clear()
                                    for (x in snap.children) {
                                        if (vehicleIdList.contains(x.key)) {
                                            vehicleList.add(x.getValue(Vehicle::class.java)!!)
                                        }
                                    }

                                    if(vehicleList.isEmpty()) {
                                        ivRentedCarsProfile.visibility = View.GONE
                                        tvRentedCarsName.visibility = View.GONE
                                        btnRentedCarsChat.visibility = View.GONE
                                    } else {
                                        ivRentedCarsProfile.visibility = View.VISIBLE
                                        tvRentedCarsName.visibility = View.VISIBLE
                                        btnRentedCarsChat.visibility = View.VISIBLE
                                    }
                                    rentedCarsPagerAdapter = MyCarsPagerAdapter(activity!!, vehicleList)
                                    viewPagerRentedCars.adapter = rentedCarsPagerAdapter
                                    viewPagerRentedCars.clipToPadding = false
                                    viewPagerRentedCars.setPadding(50, 0, 50, 0)
                                }

                            }

                            override fun onCancelled(p0: DatabaseError?) {}

                        })
                    }

                })
    }

}