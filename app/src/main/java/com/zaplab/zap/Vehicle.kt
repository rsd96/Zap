package com.zaplab.zap

/**
 * Created by Ramshad on 4/6/18.
 */

enum class Transmission{ Auto, Manual }

data class Vehicle (var ownerId: String = "", var make: String = "", var model: String = "",
                    var desc: String = "", var color: Int = 1, var transmission: Transmission = Transmission.Auto,
                    var odometer: Int = 0, var availability: AvailableDates = AvailableDates(), var lat: String = "23.23",
                    var long: String = "34.45", var city: String = "wollongong", var country: String = "Australia",
                    var rent: Int = 70, var bond: Int = 25, var rating: Double = 2.5, var imageUrl: String = "asdfsd") {
}
