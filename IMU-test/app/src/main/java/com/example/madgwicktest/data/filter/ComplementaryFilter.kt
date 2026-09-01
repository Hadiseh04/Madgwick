package com.example.madgwicktest.data.filter

import com.example.madgwicktest.data.model.Orientation
import kotlin.math.PI

class ComplementaryFilter(
    private val alpha: Float = 0.98f
) {

    private var pitch = 0f
    private var roll = 0f
    private var yaw = 0f

    fun update(
        accPitch: Float,
        accRoll: Float,
        gx: Float,
        gy: Float,
        gz: Float,
        dt: Float
    ): Orientation {


        val gxDeg = gx * 180f / PI.toFloat()
        val gyDeg = gy * 180f / PI.toFloat()
        val gzDeg = gz * 180f / PI.toFloat()


        roll =
            alpha * (roll + gxDeg * dt) +
                    (1f - alpha) * accRoll

        pitch =
            alpha * (pitch + gyDeg * dt) +
                    (1f - alpha) * accPitch


        yaw += gzDeg * dt

        return Orientation(
            pitch = pitch,
            roll = roll,
            yaw = yaw
        )
    }
}