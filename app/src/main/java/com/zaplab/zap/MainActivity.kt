package com.zaplab.zap

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by Ramshad on 11/9/17.
 *
 * Home page
 */

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"
    lateinit var auth: FirebaseAuth
    var dbRef = FirebaseDatabase.getInstance().reference
    var vehicleList = mutableListOf<Vehicle>()
    var idList = mutableListOf<String>()
    lateinit var adapter: HomeListRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        auth = FirebaseAuth.getInstance()
        checkIfUserExists()

        //startActivity(Intent(this, DummyData::class.java))
    }


    /**
     * Open filter options when user clicks the floating action button
     */
    private fun startFilterFragment() {
        val dialogFrag = FilterFragment()
        dialogFrag.setParentFab(fabHomeFilter)
        dialogFrag.show(supportFragmentManager, dialogFrag.tag)
    }

    /**
     * Get all vehicles from database and populate the list
     */

    private fun populateVehicleListing() {

        adapter = HomeListRecyclerAdapter(applicationContext, vehicleList, idList)
        rvListingHome.layoutManager = LinearLayoutManager(this)
        dbRef.child("Vehicles").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snap: DataSnapshot?) {
                snap?.let {
                    vehicleList.clear()
                    idList.clear()
                    for (x in snap.children) {
                        if (x.child("ownerId").value.toString() != (application as Global).currentUser.uid) {
                            vehicleList.add(x.getValue(Vehicle::class.java)!!)
                            idList.add(x.key)
                        }
                    }
                    rvListingHome.adapter = adapter
                    adapter.notifyDataSetChanged()
                }

            }

            override fun onCancelled(p0: DatabaseError?) {}
        })
    }

    /**
     * Filter the listing with passed criterias that are not null
     */
    fun filter(country: String, cities: MutableList<String>, makeList: MutableList<String>, modelList: MutableList<String>,
               minRent: Double, maxRent: Double) {

        if (country.isBlank() && cities.isEmpty() && makeList.isEmpty() && modelList.isEmpty() && minRent == 0.00 && maxRent == 0.00) {
            populateVehicleListing()
        } else {

            if (country.isNotBlank() && cities.isNotEmpty()) {

                val it = vehicleList.iterator()
                while (it.hasNext()) {
                    if (!cities.contains(it.next().city))
                        it.remove()
                }
            }

            if (makeList.isNotEmpty()) {
                val it = vehicleList.iterator()
                while (it.hasNext()) {
                    if (!makeList.contains(it.next().make))
                        it.remove()
                }
            }

            if (modelList.isNotEmpty()) {
                val it = vehicleList.iterator()
                while (it.hasNext()) {
                    if (!modelList.contains(it.next().model))
                        it.remove()
                }
            }

            if (minRent != 0.00) {
                val it = vehicleList.iterator()
                while (it.hasNext()) {
                    if (it.next().rent < minRent)
                        it.remove()
                }
            }

            if (maxRent != 0.00) {
                val it = vehicleList.iterator()
                while (it.hasNext()) {
                    if (it.next().rent > maxRent)
                        it.remove()
                }
            }

            adapter.notifyDataSetChanged()
        }
    }


    /**
     * Check if user is already logged in
     * if not - start loging activty
     * if yes - get user info
     */
    private fun checkIfUserExists() {
        Log.d(TAG, "checkIfUserExists()")
        var user = auth.currentUser
        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            (this.application as Global).currentUser.email = user.email.toString()
            (this.application as Global).currentUser.uid = user.uid
            (this.application as Global).currentUser.photoUrl = user.photoUrl.toString()
            if ((this.application as Global).currentUser.userName.isBlank()) {
                if (!user.displayName.isNullOrBlank()) {
                    (this.application as Global).currentUser.userName = user.displayName.toString()
                }
            }

            Log.d(TAG, (this.application as Global).currentUser.userName)
            dbRef.child("Users").child((this.application as Global).currentUser.uid).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError?) {}

                override fun onDataChange(snap: DataSnapshot?) {
                    Log.d(TAG, snap?.toString())
                    if ( snap?.value == null) {
                        Log.d("MainActivity", "User EXists!!!")
                        addUserToDB((application as Global).currentUser)
                    } else if (snap != null) {
                        (application as Global).currentUser.userName = snap.child("userName").value.toString()
                        (application as Global).currentUser.photoUrl = snap.child("photoUrl").value.toString()
                    }
                    checkIfUserIsVerified()
                }
            })
        }
    }


    /**
     * If user logs in for the first time, add their details to database
     */
    private fun addUserToDB(currentUser: User) {

        var userExists = false
        var map = hashMapOf<String, Any>()
        map.put("userName", currentUser.userName)
        map.put("email", currentUser.email)
        map.put("photoUrl", currentUser.photoUrl)
        map.put("verified", currentUser.verified)
        map.put("verificationId", currentUser.verificationId)
        map.put("rating", currentUser.rating)
        map.put("rateSum", currentUser.rateSum)
        var ref = FirebaseDatabase.getInstance().reference
        ref.child("Users").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(snap: DataSnapshot?) {
                if (snap != null) {
                    for ( x in snap.children) {
                        if(x.value == currentUser.uid) {
                            userExists = true
                            break
                        }
                    }
                    if (!userExists) {
                        FirebaseDatabase.getInstance().reference.child("Users").child(currentUser.uid).setValue(map).addOnCompleteListener {
                            checkIfUserIsVerified()
                        }
                    }
                }
            }

        })
    }

    /**
     * Check if user has already done their credit check
     * If not start credit check activity
     */
    private fun checkIfUserIsVerified() {
        var isCool = true
        dbRef.child("Users").child((this.application as Global).currentUser.uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {}

            override fun onDataChange(snap: DataSnapshot?) {
                for ( x in snap?.children!!) {
                    if ( x.key == "verified") {
                        Log.d(TAG, x.key)
                        isCool = x.value as Boolean
                        Log.d(TAG, isCool.toString())
                    }
                }

                if (!isCool) {
                    startActivity(Intent(applicationContext, VerificationActivity::class.java))
                    finish()
                    return
                } else {
                    populateVehicleListing()
                    fabHomeFilter.setOnClickListener({
                        startFilterFragment()
                    })
                }

            }

        })
    }

    /**
     * Populate Menu options
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /**
     * handle menu option clicks (profile, messages and history)
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        createMenuDialog()
        return true
    }

    /**
     * Create dialog to show user profile, messaging and history menu option
     */
    fun createMenuDialog() {
        var dialog = Dialog(this)
        dialog.setContentView(R.layout.menu_layout)
        var tvProfile: TextView = dialog.findViewById(R.id.tvMenuProfile)
        tvProfile.setOnClickListener({
            dialog.dismiss()
            startActivity(Intent(this, UserActivity::class.java))
        })

        var tvMessages: TextView = dialog.findViewById(R.id.tvMenuMessages)
        tvMessages.setOnClickListener({
            dialog.dismiss()
            startActivity(Intent(this, MessageListActivity::class.java))
        })

        var tvHistory: TextView = dialog.findViewById(R.id.tvMenuHistory)
        tvHistory.setOnClickListener({
            dialog.dismiss()
            startActivity(Intent(this, HistoryActivity::class.java))
        })

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.getWindow().getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = this.window.decorView.height/2
        dialog.window.attributes = lp
        dialog.show()
    }
}
