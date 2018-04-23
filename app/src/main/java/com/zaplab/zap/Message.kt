package com.zaplab.zap

import java.io.Serializable

/**
 * Created by Ramshad on 4/23/18.
 */
data class Message(var message: String = "",
                   var from: String = "") : Serializable