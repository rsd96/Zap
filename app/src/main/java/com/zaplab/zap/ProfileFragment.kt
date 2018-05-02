package com.zaplab.zap

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*

/**
 * Created by Ramshad on 4/8/18.
 */
class ProfileFragment: Fragment() {

    var user = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        loadProfile()
        tvProfileMail.text = (activity?.application as Global).currentUser.email
        btnLogout.setOnClickListener({
            signout()
        })
    }

    /**
     * Load and populate user profile info.
     */
    private fun loadProfile() {
        var name = (activity?.application as Global).currentUser.userName
        var imageUrl = (activity?.application as Global).currentUser.photoUrl
        Log.d("Profile Fragment", name)


        if (imageUrl.isNotBlank()) {
            // Load user profile image
            Picasso.with(activity)
                    .load(imageUrl)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(ivProfileImage, object : Callback {
                        override fun onSuccess() {

                        }

                        override fun onError() {
                            // Try again online if cache failed
                            Picasso.with(activity)
                                    .load(imageUrl)
                                    .into(ivProfileImage, object : Callback {
                                        override fun onSuccess() {

                                        }

                                        override fun onError() {

                                        }
                                    })
                        }
                    })
        }

        tvProfileName.text = name
    }


    /**
     * Signout user
     */
    private fun signout() {
        FirebaseAuth.getInstance().signOut()
        LoginManager.getInstance().logOut()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        val googleSignInClient = GoogleSignIn.getClient(activity?.applicationContext!!, gso)
        googleSignInClient.signOut()
        startActivity(Intent(activity, LoginActivity::class.java))
    }
}