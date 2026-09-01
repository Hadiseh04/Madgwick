package com.example.madgwicktest.data.filter

import com.example.madgwicktest.data.model.Orientation
import kotlin.math.atan2
import kotlin.math.sqrt

class AccelerometerFilter(
    private val lambda: Float = 0.1f
) {

    private var initialized = false

    private var filteredAx = 0f
    private var filteredAy = 0f
    private var filteredAz = 0f

    fun calculate(
        ax: Float,
        ay: Float,
        az: Float
    ): Orientation {

        if (!initialized) {
            filteredAx = ax
            filteredAy = ay
            filteredAz = az
            initialized = true
        } else {
            filteredAx =
                lambda * ax +
                        (1f - lambda) * filteredAx

            filteredAy =
                lambda * ay +
                        (1f - lambda) * filteredAy

            filteredAz =
                lambda * az +
                        (1f - lambda) * filteredAz
        }

        val pitch = Math.toDegrees(
            atan2(
                -filteredAx.toDouble(),
                sqrt(
                    (
                            filteredAy * filteredAy +
                                    filteredAz * filteredAz
                            ).toDouble()
                )
            )
        ).toFloat()

        val roll = Math.toDegrees(
            atan2(
                filteredAy.toDouble(),
                filteredAz.toDouble()
            )
        ).toFloat()

        return Orientation(
            pitch = pitch,
            roll = roll,
            yaw = 0f
        )
    }
}
