package com.zaplab.zap

/**
 * Created by Ramshad on 4/6/18.
 */

enum class Transmission{ Auto, Manual }

data class Vehicle (var ownerId: String = "", var make: String = "", var model: String = "",
                    var plate: String = "", var desc: String = "", var color: Int = 1,
                    var transmission: Transmission = Transmission.Auto, var card: CreditCard = CreditCard(),
                    var odometer: Int = 0, var availability: AvailableDates = AvailableDates(), var lat: Double = 23.23,
                    var long: Double = 34.45, var city: String = "wollongong", var country: String = "Australia",
                    var rent: Double = 70.00, var bond: Double = 25.00, var rating: Double = 2.5, var imageList: ArrayList<String> = arrayListOf()) {
}
