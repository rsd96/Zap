package com.zaplab.zap

import java.io.Serializable

/**
 * Created by Ramshad on 4/6/18.
 */
data class AvailableDates(var sunday: String = "",
                          var monday: String = "",
                          var tuesday: String = "",
                          var wednesday: String = "",
                          var thursday: String = "",
                          var friday: String = "",
                          var saturday: String = "") : Serializable