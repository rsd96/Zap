package com.zaplab.zap

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * Created by Ramshad on 4/13/18.
 */
data class User (var uid: String = "", var userName: String = "", var email: String = "", var photoUrl: String = "",
                 var verified:Boolean = false, var verificationId: String = "") {

    fun isVerified(): Boolean {
        var verified = false
        FirebaseDatabase.getInstance().reference.child("Users").addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(snap: DataSnapshot?) {
                if (snap != null) {
                    for ( x in snap.children) {
                        if(x.child("uid").value == uid && x.child("verified").value == 1) {
                            verified = true
                            break
                        }
                    }
                }
            }

        })
        return verified
    }

    fun isBlacklisted(): Boolean {
        var blackListed = false
        FirebaseDatabase.getInstance().reference.child("Blacklist").addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {}

            override fun onDataChange(snap: DataSnapshot?) {
                if (snap != null) {
                    for (x in snap.children) {
                        if (verificationId.equals(x.value)) {
                            blackListed = true
                            break
                        } else
                            blackListed = false
                    }
                }
            }
        })
        return blackListed
    }

    fun verify() {
        FirebaseDatabase.getInstance().reference.child("Users").addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(snap: DataSnapshot?) {
                if (snap != null) {
                    for ( x in snap.children) {
                        if(x.child("uid").value == uid) {
                            FirebaseDatabase.getInstance().reference.child("Users").child("verified").setValue(true)
                            break
                        }
                    }
                }
            }

        })
    }
}