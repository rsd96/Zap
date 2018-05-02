package com.zaplab.zap

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_reset_password.*

/**
 * Created by Ramshad on 11/11/17.
 */

class ResetPassActivity: AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        auth = FirebaseAuth.getInstance()

        btn_reset_password.setOnClickListener(View.OnClickListener { view ->

            val email = et_reset_email.getText().toString().trim()

            sendResetMail(email)
        })

        btn_reset_back.setOnClickListener(View.OnClickListener { view ->
            finish()
        })
    }

    private fun sendResetMail(email: String) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(application, "Enter your registered email id", Toast.LENGTH_SHORT).show()
        } else {

            reset_progressBar.visibility = View.VISIBLE
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Reset mail has been successfully sent !", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Error sending mail !", Toast.LENGTH_SHORT).show()
                        }

                        reset_progressBar.visibility = View.GONE
                    }
        }
    }
}