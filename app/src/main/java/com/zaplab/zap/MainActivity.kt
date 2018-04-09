package com.zaplab.zap

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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


        /* ANONYMOUS SIGN IN
        auth = FirebaseAuth.getInstance()
        auth.signInAnonymously()
                .addOnCompleteListener(this, object : OnCompleteListener<AuthResult> {
                    override fun onComplete(task: Task<AuthResult>) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success")
                            val user = auth.getCurrentUser()
                            updateUI(user)

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException())
                            Toast.makeText(applicationContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
                })

                **/

        var vehicleList = mutableListOf<Vehicle>()
        dbRef.child("Vehicles").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snap: DataSnapshot?) {
                snap?.let {
                    vehicleList.clear()
                    for (x in snap.children) {
                        vehicleList.add(x.getValue(Vehicle::class.java)!!)
                    }

                    if (vehicleList.isNotEmpty()) {
                        var pagerAdapter = VehicleListPagerAdapter(applicationContext, vehicleList)
                        viewPagerListingHome.adapter = pagerAdapter
                    }
                }

            }

            override fun onCancelled(p0: DatabaseError?) {}

        })
    }

    fun updateUI(user: FirebaseUser?) {
        if(user != null) {
            // TODO LOAD VEHICLE LISTING
        } else {

        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        var dialog = Dialog(this)
        dialog.setContentView(R.layout.menu_layout)
        var tvProfile: TextView = dialog.findViewById(R.id.tvMenuProfile)
        tvProfile.setOnClickListener({
            startActivity(Intent(this, UserActivity::class.java))
        })

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.getWindow().getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = this.window.decorView.height/2
        dialog.window.attributes = lp
        dialog.show()
        return true
    }
}
