package com.example.madgwicktest.domain

import com.example.madgwicktest.data.model.MatchTarget
import com.example.madgwicktest.data.model.MethodType
import com.example.madgwicktest.data.model.Orientation

class OrientationMatcher {

    fun calculateError(
        current: Orientation,
        target: MatchTarget,
        method: MethodType
    ): Float {
        val pitchError =
            AngleUtils.difference(
                current.pitch,
                target.pitch
            )

        val rollError =
            AngleUtils.difference(
                current.roll,
                target.roll
            )

        if (method == MethodType.ACCELEROMETER) {
            return (pitchError + rollError) / 2f
        }

        val yawError =
            AngleUtils.difference(
                current.yaw,
                target.yaw
            )

        return (pitchError + rollError + yawError) / 3f
    }

    fun isMatch(
        error: Float,
        tolerance: Float
    ): Boolean =
        error <= tolerance
}
