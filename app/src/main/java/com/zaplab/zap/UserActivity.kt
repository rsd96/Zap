package com.zaplab.zap

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.google.firebase.auth.FirebaseAuth
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

        setupTabs()

    }

    /**
     * Create fragments, populate tabs and handle tab selection
     */
    private fun setupTabs() {
        // Create items
        val item1 = AHBottomNavigationItem(R.string.user_tab_1, R.drawable.ic_profile, android.R.color.black)
        val item2 = AHBottomNavigationItem(R.string.user_tab_2, R.drawable.ic_car, android.R.color.black)
        val item3 = AHBottomNavigationItem(R.string.user_tab_3, R.drawable.ic_menu, android.R.color.black)
        val item4 = AHBottomNavigationItem(R.string.user_tab_4, R.drawable.ic_credit_card, android.R.color.black)

        // Add items
        bottomNavUser.addItem(item1)
        bottomNavUser.addItem(item2)
        bottomNavUser.addItem(item3)
        bottomNavUser.addItem(item4)

        bottomNavUser.defaultBackgroundColor = ContextCompat.getColor(this, R.color.colorAccent)
        bottomNavUser.accentColor = ContextCompat.getColor(this, android.R.color.white)

        // Create all fragments
        var profileFragment = ProfileFragment()
        var myCarsFragment = MyCarFragment()
        var rentedCarsFragment = RentedCarsFragment()
        var creditCardFragment = CreditCardsFragment()

        // Fragment transaction to switch fragment
        var transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, profileFragment)
        transaction.commit()


        // Handle tab selection
        bottomNavUser.setOnTabSelectedListener(object: AHBottomNavigation.OnTabSelectedListener{
            override fun onTabSelected(position: Int, wasSelected: Boolean): Boolean {
                Log.d("user", ""+ position)
                transaction = supportFragmentManager.beginTransaction()
                when (position) {
                    0 -> {
                        transaction.replace(R.id.fragmentContainer, profileFragment)
                        transaction.commit()
                    }

                    1 -> {
                        transaction.replace(R.id.fragmentContainer, myCarsFragment)
                        transaction.commit()
                    }

                    2 -> {
                        transaction.replace(R.id.fragmentContainer, rentedCarsFragment)
                        transaction.commit()
                    }

                    3 -> {
                        transaction.replace(R.id.fragmentContainer, creditCardFragment)
                        transaction.commit()
                    }
                }

                return true
            }

        })
    }
}