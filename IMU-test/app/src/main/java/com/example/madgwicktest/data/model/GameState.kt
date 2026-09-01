package com.example.madgwicktest.data.model

data class GameState(
    val settings: GameSettings = GameSettings(),
    val targets: List<MatchTarget> = emptyList(),
    val currentTargetIndex: Int = 0,
    val timeLeftSeconds: Int = 60,
    val score: Int = 0,
    val currentOrientation: Orientation = Orientation(),
    val currentError: Float = 0f,
    val isRunning: Boolean = false,
    val isFinished: Boolean = false
) {
    val currentTarget: MatchTarget?
        get() =
            if (targets.isEmpty()) {
                null
            } else {
                targets[currentTargetIndex % targets.size]
            }
}
