package com.example.madgwicktest.data.model

data class GameSettings(
    val method: MethodType = MethodType.MADGWICK,
    val durationSeconds: Int = 60,
    val toleranceDegrees: Float = 15f
)
