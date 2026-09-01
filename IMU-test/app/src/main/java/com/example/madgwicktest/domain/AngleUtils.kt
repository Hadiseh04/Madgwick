package com.example.madgwicktest.domain

import kotlin.math.abs

object AngleUtils {

    fun difference(
        a: Float,
        b: Float
    ): Float {
        var diff = a - b

        while (diff > 180f) {
            diff -= 360f
        }

        while (diff < -180f) {
            diff += 360f
        }

        return abs(diff)
    }
}
