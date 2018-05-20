package com.zaplab.zap

import java.io.Serializable

/**
 * Created by Ramshad on 4/6/18.
 *
 * Model class to hold vehicle information
 */

enum class Transmission{ Auto, Manual }

data class Vehicle (var ownerId: String = "", var make: String = "", var model: String = "",
                    var plate: String = "", var desc: String = "", var color: Int = 1,
                    var transmission: Transmission = Transmission.Auto, var card: CreditCard = CreditCard(),
                    var odometer: Int = 0, var availability: AvailableDates = AvailableDates(), var lat: Double = 0.00,
                    var long: Double = 0.00, var city: String = "wollongong", var country: String = "Australia",
                    var rent: Double = 0.00, var imageList: ArrayList<String> = arrayListOf(),
                    var amenities: List<String> = listOf()) : Serializable {
}
