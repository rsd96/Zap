package com.zaplab.zap

import java.io.Serializable

/**
 * Created by Ramshad on 4/23/18.
 *
 * Class that defines a unique chat channel between two users
 */
data class Channel(var user1: String = "",
                   var user2: String = "",
                   var lastMessage: String = "" )
    : Serializable