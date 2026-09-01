package com.example.madgwicktest.domain

import com.example.madgwicktest.data.model.MatchTarget

class TargetGenerator {

    fun defaultSequence(): List<MatchTarget> =
        listOf(
            MatchTarget(
                id = 1,
                pitch = 10f,
                roll = 0f,
                yaw = 0f
            ),
            MatchTarget(
                id = 2,
                pitch = -10f,
                roll = 15f,
                yaw = 30f
            ),
            MatchTarget(
                id = 3,
                pitch = 20f,
                roll = -15f,
                yaw = -45f
            ),
            MatchTarget(
                id = 4,
                pitch = -20f,
                roll = -10f,
                yaw = 60f
            ),
            MatchTarget(
                id = 5,
                pitch = 0f,
                roll = 20f,
                yaw = -90f
            )
        )
}
