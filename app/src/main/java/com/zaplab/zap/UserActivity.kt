package com.zaplab.zap

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user.*




/**
 * Created by Ramshad on 4/5/18.
 */
class UserActivity: AppCompatActivity() {

    private val RC_SIGN_IN = 123
    var user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        // Create items
        val item1 = AHBottomNavigationItem(R.string.user_tab_1, R.drawable.ic_profile, android.R.color.black)
        val item2 = AHBottomNavigationItem(R.string.user_tab_2, R.drawable.ic_chat, android.R.color.black)
        val item3 = AHBottomNavigationItem(R.string.user_tab_3, R.drawable.ic_menu, android.R.color.black)

        // Add items
        bottomNavUser.addItem(item1)
        bottomNavUser.addItem(item2)
        bottomNavUser.addItem(item3)

        bottomNavUser.defaultBackgroundColor = ContextCompat.getColor(this, R.color.colorAccent)
        bottomNavUser.accentColor = ContextCompat.getColor(this, android.R.color.white)


        var name = user?.displayName
        var imageUrl = user?.photoUrl

        // Load user profile image
        Picasso.with(applicationContext)
                .load(imageUrl)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(ivProfileImage, object : Callback {
                    override fun onSuccess() {

                    }

                    override fun onError() {
                        // Try again online if cache failed
                        Picasso.with(applicationContext)
                                .load(imageUrl)
                                .into(ivProfileImage, object : Callback {
                                    override fun onSuccess() {

                                    }

                                    override fun onError() {

                                    }
                                })
                    }
                })

        tvProfileName.text = name

        // setup tabs and viewpager

//        var tabsAdapter = FragmentAdapter(supportFragmentManager)
//        tabsAdapter.addFragment(ProfileFragment())
//        tabsAdapter.addFragment(MyCarFragment())
//        tabsAdapter.addFragment(RentedCarsFragment())

        var profileFragment = ProfileFragment()
        var myCarsFragment = MyCarFragment()
        var rentedCarsFragment = RentedCarsFragment()

        var transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentProfile, profileFragment)
        transaction.commit()


        bottomNavUser.setOnTabSelectedListener(object: AHBottomNavigation.OnTabSelectedListener{
            override fun onTabSelected(position: Int, wasSelected: Boolean): Boolean {
                Log.d("user", ""+ position)
                transaction = supportFragmentManager.beginTransaction()
                when (position) {
                    0 -> {
                        transaction.replace(R.id.fragmentProfile, profileFragment)
                        transaction.commit()
                    }

                    1 -> {
                        transaction.replace(R.id.fragmentProfile, myCarsFragment)
                        transaction.commit()
                    }

                    2 -> {
                        transaction.replace(R.id.fragmentProfile, rentedCarsFragment)
                        transaction.commit()
                    }
                }

                return true
            }

        })

//        btnAddCar.setOnClickListener({
//            var user = FirebaseAuth.getInstance().currentUser
//            if(user == null) {
//                val providers = Arrays.asList(
//                        AuthUI.IdpConfig.EmailBuilder().build(),
//                        AuthUI.IdpConfig.GoogleBuilder().build(),
//                        AuthUI.IdpConfig.FacebookBuilder().build())
//
//                // Create and launch sign-in intent
//                startActivityForResult(
//                        AuthUI.getInstance()
//                                .createSignInIntentBuilder()
//                                .setAvailableProviders(providers)
//                                .setTheme(R.style.AppTheme_NoActionBar)
//                                .build(),
//                        RC_SIGN_IN)
//            } else {
//                //TODO START ACTIVITY TO GET VEHICLE INFO
//                //startActivity(Intent(this, AddCarActivity::class.java))
//                var car = Vehicle(ownerId = FirebaseAuth.getInstance().currentUser?.uid!!, name = "Tesla", desc = "good stuff",
//                        color = "red", transmission = "auto", odometer = 500, availability = AvailableDates(), lat = "sdf",
//                        long = "efds", city = "wollongong", country = "Australia", rent = 50, bond = 25, rating = 2.5, imageUrl = "asdf")
//                FirebaseDatabase.getInstance().reference.child("Vehicles").push().setValue(car)
//            }
//        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    finish()
                }
            } else {
                // Sign in failed, check response for error code
                // ...
            }
        }
    }
}