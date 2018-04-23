package com.zaplab.zap

import android.app.Application
import com.google.firebase.FirebaseApp

/**
 * Created by Ramshad on 4/23/18.
 */
class Global: Application() {


    var currentUser = User()

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}