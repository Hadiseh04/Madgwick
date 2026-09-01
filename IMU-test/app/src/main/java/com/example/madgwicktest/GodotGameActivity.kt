package com.example.madgwicktest

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.madgwicktest.data.model.MethodType
import com.example.madgwicktest.data.repository.SensorRepository
import com.example.madgwicktest.godot.OrientationBridgePlugin
import kotlinx.coroutines.launch
import org.godotengine.godot.Godot
import org.godotengine.godot.GodotActivity
import org.godotengine.godot.plugin.GodotPlugin
import android.os.Build
import android.view.Surface
import android.view.WindowInsets
import android.view.WindowInsetsController

class GodotGameActivity : GodotActivity() {

    private lateinit var repository: SensorRepository
    private var sensorsStarted = false
    private var method: MethodType = MethodType.MADGWICK
    private var canReturnResult = false

    private var resultReturned = false


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        Log.d(TAG, "onCreate")

        requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        method =
            intent.getStringExtra(EXTRA_METHOD)
                ?.let(MethodType::valueOf)
                ?: MethodType.MADGWICK

        val durationSeconds =
            intent.getIntExtra(
                EXTRA_DURATION_SECONDS,
                60
            )

        OrientationBridgePlugin.configureGame(
            method = method.displayName(),
            duration = durationSeconds
        )

        OrientationBridgePlugin.setResultListener { score, finalError ->
            runOnUiThread {
                if (!canReturnResult || resultReturned) {
                    return@runOnUiThread
                }

                resultReturned = true

                Log.d(
                    TAG,
                    "Result received score=$score finalError=$finalError"
                )

                val resultIntent =
                    Intent()
                        .putExtra(EXTRA_SCORE, score)
                        .putExtra(EXTRA_FINAL_ERROR, finalError)

                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }

        super.onCreate(savedInstanceState)
        hideSystemBars()

        repository =
            SensorRepository(this, currentDisplayRotation())

        repository.setMethod(method)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                repository.orientation.collect { orientation ->
                    OrientationBridgePlugin.updateOrientation(
                        pitch = orientation.pitch,
                        roll = orientation.roll,
                        yaw = orientation.yaw
                    )
                }
            }
        }
    }

    private fun currentDisplayRotation(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display?.rotation ?: Surface.ROTATION_0
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.rotation
        }
    }

    private fun hideSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(
                WindowInsets.Type.statusBars() or
                        WindowInsets.Type.navigationBars()
            )

            window.insetsController?.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility =
                android.view.View.SYSTEM_UI_FLAG_FULLSCREEN or
                        android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }

    override fun onGodotMainLoopStarted() {
        super.onGodotMainLoopStarted()

        Log.d(TAG, "onGodotMainLoopStarted")

        repository.setMethod(method)

        if (!sensorsStarted) {
            repository.start()
            sensorsStarted = true
        }

        canReturnResult = true
    }

    override fun getCommandLine(): MutableList<String> {
        return super.getCommandLine().toMutableList().apply {
            add("--main-pack")
            add("res://IMU-test.pck")
        }
    }

    override fun onGodotForceQuit(
        godot: Godot
    ) {
        Log.w(TAG, "Godot requested force quit. resultReturned=$resultReturned")

        if (!resultReturned) {
            runOnUiThread {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }
    }

    override fun getHostPlugins(
        godot: Godot
    ): Set<GodotPlugin> =
        setOf(
            OrientationBridgePlugin(godot)
        )

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")

        OrientationBridgePlugin.setResultListener(null)

        if (::repository.isInitialized && sensorsStarted) {
            repository.stop()
        }

        super.onDestroy()
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    private fun MethodType.displayName(): String =
        when (this) {
            MethodType.ACCELEROMETER -> "Accelerometer + EWMA"
            MethodType.COMPLEMENTARY -> "Complementary filter"
            MethodType.MADGWICK -> "Madgwick MARG"
        }

    companion object {
        private const val TAG = "GodotGameActivity"
        const val EXTRA_METHOD = "method"
        const val EXTRA_DURATION_SECONDS = "duration_seconds"
        const val EXTRA_SCORE = "score"
        const val EXTRA_FINAL_ERROR = "final_error"
    }
}