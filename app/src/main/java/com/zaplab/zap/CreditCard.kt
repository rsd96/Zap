package com.zaplab.zap

import java.io.Serializable

/**
 * Created by Ramshad on 4/13/18.
 */
data class CreditCard (var cardHolder: String = "", var number: String = "", var name: String = "", var exp: String = "",
                       var cvv: String = "") : Serializable{
}