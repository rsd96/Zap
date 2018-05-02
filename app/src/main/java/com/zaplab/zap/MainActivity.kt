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

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"
    lateinit var auth: FirebaseAuth
    var dbRef = FirebaseDatabase.getInstance().reference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        auth = FirebaseAuth.getInstance()
        if(checkIfUserExists()) {
            if(checkIfUserIsVerified()) {
                populateVehicleListing()
                fabHomeFilter.setOnClickListener({
                    startFilterFragment()
                })
            }
        }
    }

    private fun startFilterFragment() {
        val dialogFrag = FilterFragment()
        dialogFrag.setParentFab(fabHomeFilter)
        dialogFrag.show(supportFragmentManager, dialogFrag.tag)
    }

    // Get all vehicles from database and populate the list
    private fun populateVehicleListing() {
        var vehicleList = mutableListOf<Vehicle>()
        var adapter = HomeListRecyclerAdapter(applicationContext, vehicleList)
        rvListingHome.layoutManager = LinearLayoutManager(this)
        dbRef.child("Vehicles").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snap: DataSnapshot?) {
                snap?.let {
                    vehicleList.clear()
                    for (x in snap.children) {
                        if (x.child("ownerId").value.toString() != (application as Global).currentUser.uid)
                            vehicleList.add(x.getValue(Vehicle::class.java)!!)
                    }

                    if (vehicleList.isNotEmpty()) {

                        rvListingHome.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
                }

            }

            override fun onCancelled(p0: DatabaseError?) {}

        })
    }

    // Check if user is already logged in
    // if not - start loging activty
    // if yes - get user info
    private fun checkIfUserExists() : Boolean {
        var user = auth.currentUser
        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return false
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
                    if ( snap != null && !snap.exists()) {
                        (application as Global).currentUser.addUserToDB()
                    }
                }

            })
            return true
        }
    }

    // Check if user has already done their credit check
    private fun checkIfUserIsVerified() : Boolean {
        var isCool = true
        dbRef.child("Users").child((this.application as Global).currentUser.uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {}

            override fun onDataChange(snap: DataSnapshot?) {
                for ( x in snap?.children!!) {
                    if ( x.key == "verified") {
                        isCool = x.value as Boolean
                    }
                }

                if (!isCool) {
                    startActivity(Intent(applicationContext, VerificationActivity::class.java))
                    finish()
                    return
                }

            }

        })

        return isCool
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        createMenuDialog()
        return true
    }

    // Create dialog to show user profile, messaging menu option
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

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.getWindow().getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = this.window.decorView.height/2
        dialog.window.attributes = lp
        dialog.show()
    }
}
