package com.zaplab.zap

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_credit_check.*

/**
 * Created by Ramshad on 4/16/18.
 */
class VerificationActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_check)

        btnCreditCheck.setOnClickListener({
            btnCreditCheck.startLoading()
            MainActivity.currentUser.verificationId = etCreditCheck.text.toString().trim()
            if (MainActivity.currentUser.isBlacklisted())
                btnCreditCheck.loadingFailed()
            else {
                MainActivity.currentUser.verify()
                startActivity(Intent(this, MainActivity::class.java))
            }
        })
    }
}