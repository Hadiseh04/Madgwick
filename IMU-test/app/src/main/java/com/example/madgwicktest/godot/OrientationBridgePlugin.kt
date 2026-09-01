package com.example.madgwicktest.godot

import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo
import org.godotengine.godot.plugin.UsedByGodot

class OrientationBridgePlugin(
    godot: Godot
) : GodotPlugin(godot) {

    companion object {
        private var instance: OrientationBridgePlugin? = null
        private var resultListener: ((Int, Float) -> Unit)? = null
        private var methodName: String = "Madgwick MARG"
        private var durationSeconds: Int = 60


        fun setResultListener(
            listener: ((Int, Float) -> Unit)?
        ) {
            resultListener = listener
        }

        fun configureGame(
            method: String,
            duration: Int
        ) {
            methodName = method
            durationSeconds = duration
            instance?.emitGameConfig()
        }

        fun updateOrientation(
            pitch: Float,
            roll: Float,
            yaw: Float
        ) {
            instance?.emitOrientation(
                pitch = pitch,
                roll = roll,
                yaw = yaw
            )
        }
    }

    override fun getPluginName(): String =
        "OrientationBridge"

    @Deprecated("Deprecated in Godot Android API, but still required for exposing methods.")
    override fun getPluginMethods(): List<String> =
        listOf(
            "isReady",
            "getMethodName",
            "getDurationSeconds",
            "submitResult",
            "is_ready",
            "get_method_name",
            "get_duration_seconds",
            "submit_result"
        )

    override fun getPluginSignals(): Set<SignalInfo> =
        setOf(
            SignalInfo(
                "orientation_updated",
                java.lang.Float::class.java,
                java.lang.Float::class.java,
                java.lang.Float::class.java
            ),
            SignalInfo(
                "game_configured",
                String::class.java,
                java.lang.Integer::class.java
            )
        )

    override fun onGodotMainLoopStarted() {
        instance = this
        emitGameConfig()
    }

    override fun onGodotTerminating() {
        if (instance == this) {
            instance = null
        }
    }

    @UsedByGodot
    fun isReady(): Boolean =
        instance == this

    @UsedByGodot
    fun is_ready(): Boolean =
        isReady()

    @UsedByGodot
    fun getMethodName(): String =
        methodName

    @UsedByGodot
    fun get_method_name(): String =
        getMethodName()

    @UsedByGodot
    fun getDurationSeconds(): Int =
        durationSeconds

    @UsedByGodot
    fun get_duration_seconds(): Int =
        getDurationSeconds()

    @UsedByGodot
    fun submitResult(
        score: Int,
        finalError: Float
    ) {
        resultListener?.invoke(
            score,
            finalError
        )
    }

    @UsedByGodot
    fun submit_result(
        score: Int,
        finalError: Float
    ) {
        submitResult(
            score = score,
            finalError = finalError
        )
    }

    private fun emitOrientation(
        pitch: Float,
        roll: Float,
        yaw: Float
    ) {
        emitSignal(
            "orientation_updated",
            pitch,
            roll,
            yaw
        )
    }

    private fun emitGameConfig() {
        emitSignal(
            "game_configured",
            methodName,
            durationSeconds
        )
    }
}
