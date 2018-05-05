package com.zaplab.zap

import java.io.Serializable

/**
 * Created by Ramshad on 5/5/18.
 */
data class RateReview(var user: String = "",
                      var rater: String = "",
                      var rating: Double = 0.00,
                      var review: String = ""): Serializable {
}