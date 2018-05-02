package com.zaplab.zap

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_credit_check.*

/**
 * Created by Ramshad on 4/16/18.
 */
class VerificationActivity: AppCompatActivity() {

    var TAG = "Verify"
    var licence = ""
    var blackListed = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_check)

        btnCreditCheck.setOnClickListener({
            btnCreditCheck.startLoading()
            licence = etCreditCheck.text.toString().trim()
            blackListed = false
            verify()
        })
    }


    /**
     * Get license number and verify user if not blacklisted
     */
    private fun verify() {
        // check if user's license is blacklisted
        FirebaseDatabase.getInstance().reference.child("Blacklist").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {}

            override fun onDataChange(snap: DataSnapshot?) {
                if (snap != null) {
                    for (x in snap.children) {
                        Log.d(TAG, "licence - $licence == ${x.value.toString()}")
                        if (licence == x.value.toString()) {
                            blackListed = true
                            break
                        }
                    }
                }

                Log.d(TAG, "black " + blackListed.toString())
                if (blackListed) {
                    btnCreditCheck.loadingFailed()
                    Toast.makeText(applicationContext, "Credit value not sufficient to access the app! ", Toast.LENGTH_SHORT).show()
                } else {
                    btnCreditCheck.loadingSuccessful()
                    var updateVerif = hashMapOf<String, Any>()

                    // Update verification info in user's database
                    updateVerif.put("verified", true)
                    updateVerif.put("verificationId", licence)
                    FirebaseDatabase.getInstance().reference.child("Users").child((application as Global).currentUser.uid)
                            .updateChildren(updateVerif).addOnCompleteListener {
                                startActivity(Intent(applicationContext, MainActivity::class.java))
                                finish()
                            }
                }

            }
        })
    }
}