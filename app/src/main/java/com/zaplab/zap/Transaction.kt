package com.zaplab.zap

import java.io.Serializable

/**
 * Created by Ramshad on 5/2/18.
 */
data class Transaction(var transactionDate: String = "",
                       var owner: String = "",
                       var renter: String = "",
                       var fromDate: String = "",
                       var toDate: String = "",
                       var vehicleId: String = "",
                       var renterCard: String = "",
                       var amount: Double = 0.00,
                       var isDamagePaidByInsurance: Boolean = false): Serializable{

}