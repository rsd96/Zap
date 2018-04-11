package com.zaplab.zap

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profile.*

/**
 * Created by Ramshad on 4/8/18.
 */
class ProfileFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btnLogout.setOnClickListener({
            FirebaseAuth.getInstance().signOut()
            LoginManager.getInstance().logOut()
            startActivity(Intent(activity, LoginActivity::class.java))
        })
    }
}