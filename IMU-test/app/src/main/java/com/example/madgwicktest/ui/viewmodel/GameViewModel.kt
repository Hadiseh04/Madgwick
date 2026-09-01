package com.example.madgwicktest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.madgwicktest.data.model.GameSettings
import com.example.madgwicktest.data.model.GameState
import com.example.madgwicktest.data.model.MethodType
import com.example.madgwicktest.data.model.Orientation
import com.example.madgwicktest.domain.OrientationMatcher
import com.example.madgwicktest.domain.TargetGenerator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val targetGenerator =
        TargetGenerator()

    private val matcher =
        OrientationMatcher()

    private var timerJob: Job? = null
    private var matchLocked = false

    private val _gameState =
        MutableStateFlow(
            GameState(
                targets = targetGenerator.defaultSequence()
            )
        )

    val gameState: StateFlow<GameState> =
        _gameState

    fun configure(
        method: MethodType,
        durationSeconds: Int
    ) {
        val settings =
            GameSettings(
                method = method,
                durationSeconds = durationSeconds
            )

        _gameState.value =
            GameState(
                settings = settings,
                targets = targetGenerator.defaultSequence(),
                timeLeftSeconds = durationSeconds
            )
    }

    fun start() {
        timerJob?.cancel()
        matchLocked = false

        _gameState.update {
            it.copy(
                isRunning = true,
                isFinished = false,
                score = 0,
                currentTargetIndex = 0,
                timeLeftSeconds = it.settings.durationSeconds
            )
        }

        timerJob =
            viewModelScope.launch {
                while (
                    _gameState.value.timeLeftSeconds > 0 &&
                    _gameState.value.isRunning
                ) {
                    delay(1000)

                    _gameState.update {
                        it.copy(
                            timeLeftSeconds = it.timeLeftSeconds - 1
                        )
                    }
                }

                finish()
            }
    }

    fun onOrientationChanged(
        orientation: Orientation
    ) {
        val state = _gameState.value
        val target = state.currentTarget ?: return

        val error =
            matcher.calculateError(
                current = orientation,
                target = target,
                method = state.settings.method
            )

        _gameState.update {
            it.copy(
                currentOrientation = orientation,
                currentError = error
            )
        }

        if (
            state.isRunning &&
            !matchLocked &&
            matcher.isMatch(
                error = error,
                tolerance = state.settings.toleranceDegrees
            )
        ) {
            matchLocked = true
            advanceTarget()
        }

        if (
            matchLocked &&
            error > state.settings.toleranceDegrees * 1.5f
        ) {
            matchLocked = false
        }
    }

    fun setExternalResult(
        score: Int,
        finalError: Float
    ) {
        timerJob?.cancel()

        _gameState.update {
            it.copy(
                score = score,
                currentError = finalError,
                isRunning = false,
                isFinished = true,
                timeLeftSeconds = 0
            )
        }
    }

    private fun advanceTarget() {
        _gameState.update {
            it.copy(
                score = it.score + 1,
                currentTargetIndex = it.currentTargetIndex + 1
            )
        }
    }

    private fun finish() {
        timerJob?.cancel()

        _gameState.update {
            it.copy(
                isRunning = false,
                isFinished = true
            )
        }
    }
}
