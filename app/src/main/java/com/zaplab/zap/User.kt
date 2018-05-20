package com.zaplab.zap

/**
 * Created by Ramshad on 4/13/18.
 *
 * Model class to hold user data
 */
data class User (var uid: String = "", var userName: String = "", var email: String = "", var photoUrl: String = "",
                 var verified:Boolean = false, var verificationId: String = "", var rating: Double = 0.00, var rateSum: Int = 0) {
}