package com.zaplab.zap

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_signup.*

/**
 * Created by Ramshad on 11/11/17.
 */
class SignUpActivity: AppCompatActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()

        btn_sign_in.setOnClickListener(View.OnClickListener { view ->
            finish()
        })

        btn_signup_reset_password.setOnClickListener({ view ->
            startActivity(Intent(this, ResetPassActivity::class.java))
        })

        btn_sign_up.setOnClickListener({ view ->
            val userName = et_signup_username.text.toString().trim()
            val email = et_signup_email.text.toString().trim()
            val password = et_signup_password.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(applicationContext, "Enter email address !", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(applicationContext, "Enter password !", Toast.LENGTH_SHORT).show()
            } else if (password.length < 6) {
                Toast.makeText(applicationContext, "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show()

            } else {

                signup_progressBar.visibility = View.GONE
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, { task ->
                            Toast.makeText(this, "createUserWithEmail:" + task.isSuccessful, Toast.LENGTH_SHORT).show()
                            signup_progressBar.setVisibility(View.GONE)

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (task.isSuccessful) {
                                var ref = FirebaseDatabase.getInstance().getReference()
                                ref.child("Users").child(auth.currentUser?.uid).child("user_name").setValue(userName)
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this, "Authentication Failed : " + task.exception!!, Toast.LENGTH_SHORT).show()
                            }
                        })
            }
        })

    }
}