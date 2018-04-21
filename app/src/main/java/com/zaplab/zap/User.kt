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

    fun isChecked(): Boolean {

        var isCool = false
        FirebaseDatabase.getInstance().reference.child("Users").child(uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {}

            override fun onDataChange(snap: DataSnapshot?) {
                for ( x in snap?.children!!) {
                    if ( x.key == "verified") {
                        isCool = x.value as Boolean
                    }
                }
            }

        })
        return isCool
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
                        }
                    }
                }
            }
        })
        return blackListed
    }

    fun verify() {
        FirebaseDatabase.getInstance().reference.child("Users").child(uid).child("verified").setValue(true)
    }

    fun addUserToDB() {
        var userExists = false
        var map = hashMapOf<String, Any>()
        map.put("userName", userName)
        map.put("email", email)
        map.put("photoUrl", photoUrl)
        map.put("verified", verified)
        map.put("verificationId", verificationId)
        var ref = FirebaseDatabase.getInstance().reference
        ref.child("Users").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(snap: DataSnapshot?) {
                if (snap != null) {
                    for ( x in snap.children) {
                        if(x.value == uid) {
                            userExists = true
                            break
                        }
                    }
                    if (!userExists) {
                        FirebaseDatabase.getInstance().reference.child("Users").child(uid).setValue(map)
                    }
                }
            }

        })
    }
}