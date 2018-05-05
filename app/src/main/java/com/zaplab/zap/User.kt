package com.zaplab.zap

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * Created by Ramshad on 4/13/18.
 */
data class User (var uid: String = "", var userName: String = "", var email: String = "", var photoUrl: String = "",
                 var verified:Boolean = false, var verificationId: String = "", var rating: Double = 0.00, var rateSum: Int = 0) {


    fun addUserToDB() {
        var userExists = false
        var map = hashMapOf<String, Any>()
        map.put("userName", userName)
        map.put("email", email)
        map.put("photoUrl", photoUrl)
        map.put("verified", verified)
        map.put("verificationId", verificationId)
        map.put("rating", rating)
        map.put("rateSum", rateSum)
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