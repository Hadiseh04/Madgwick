package com.example.madgwicktest.data.repository

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Surface
import com.example.madgwicktest.data.filter.AccelerometerFilter
import com.example.madgwicktest.data.filter.ComplementaryFilter
import com.example.madgwicktest.data.filter.MadgwickFilter
import com.example.madgwicktest.data.model.MethodType
import com.example.madgwicktest.data.model.Orientation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SensorRepository(
    context: Context,
    private val screenRotation: Int = Surface.ROTATION_0
) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accelerometer =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val gyroscope =
        sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private val magnetometer =
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val accelerometerFilter = AccelerometerFilter()
    private val complementaryFilter = ComplementaryFilter()
    private val madgwickFilter = MadgwickFilter(
        beta = 0.03f
    )

    private var ax = 0f
    private var ay = 0f
    private var az = 0f

    private var mx = 0f
    private var my = 0f
    private var mz = 0f

    private var hasAccelerometer = false
    private var hasMagnetometer = false
    private var previousGyroTimestamp = 0L

    private var currentMethod =
        MethodType.ACCELEROMETER

    private var accResult = Orientation()
    private var compResult = Orientation()
    private var madgwickResult = Orientation()

    private val _orientation =
        MutableStateFlow(Orientation())

    val orientation: StateFlow<Orientation>
        get() = _orientation

    private val _accOrientation =
        MutableStateFlow(Orientation())

    val accOrientation: StateFlow<Orientation>
        get() = _accOrientation

    private val _compOrientation =
        MutableStateFlow(Orientation())

    val compOrientation: StateFlow<Orientation>
        get() = _compOrientation

    private val _madgwickOrientation =
        MutableStateFlow(Orientation())

    val madgwickOrientation: StateFlow<Orientation>
        get() = _madgwickOrientation

    fun setMethod(
        method: MethodType
    ) {
        currentMethod = method
        updateSelectedOrientation()
    }

    fun start() {
        sensorManager.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_GAME
        )

        sensorManager.registerListener(
            this,
            gyroscope,
            SensorManager.SENSOR_DELAY_GAME
        )

        sensorManager.registerListener(
            this,
            magnetometer,
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(
        event: SensorEvent
    ) {
        when (event.sensor.type) {

            Sensor.TYPE_ACCELEROMETER -> {
                val remapped = remapForDisplayRotation(event.values)
                ax = remapped[0]
                ay = remapped[1]
                az = remapped[2]
                hasAccelerometer = true

                accResult =
                    accelerometerFilter.calculate(
                        ax = ax,
                        ay = ay,
                        az = az
                    )

                _accOrientation.value = accResult
                updateSelectedOrientation()
            }

            Sensor.TYPE_MAGNETIC_FIELD -> {
                val remapped = remapForDisplayRotation(event.values)
                mx = remapped[0]
                my = remapped[1]
                mz = remapped[2]
                hasMagnetometer = true
            }

            Sensor.TYPE_GYROSCOPE -> {
                if (!hasAccelerometer) {
                    return
                }

                if (previousGyroTimestamp == 0L) {
                    previousGyroTimestamp = event.timestamp
                    return
                }

                val dt =
                    (event.timestamp - previousGyroTimestamp) / 1_000_000_000f

                previousGyroTimestamp = event.timestamp

                if (dt <= 0f || dt > 0.2f) {
                    return
                }

                val gyroRemapped = remapForDisplayRotation(event.values)
                val gx = gyroRemapped[0]
                val gy = gyroRemapped[1]
                val gz = gyroRemapped[2]

                compResult =
                    complementaryFilter.update(
                        accPitch = accResult.pitch,
                        accRoll = accResult.roll,
                        gx = gx,
                        gy = gy,
                        gz = gz,
                        dt = dt
                    )

                madgwickResult =
                    madgwickFilter.update(
                        ax = ax,
                        ay = ay,
                        az = az,
                        gx = gx,
                        gy = gy,
                        gz = gz,
                        mx = if (hasMagnetometer) mx else 0f,
                        my = if (hasMagnetometer) my else 0f,
                        mz = if (hasMagnetometer) mz else 0f,
                        dt = dt
                    )

                _compOrientation.value = compResult
                _madgwickOrientation.value = madgwickResult
                updateSelectedOrientation()
            }
        }
    }

    private fun remapForDisplayRotation(
        values: FloatArray
    ): FloatArray {
        val x = values[0]
        val y = values[1]
        val z = values[2]

        return when (screenRotation) {
            Surface.ROTATION_90 ->
                floatArrayOf(y, -x, z)

            Surface.ROTATION_180 ->
                floatArrayOf(-x, -y, z)

            Surface.ROTATION_270 ->
                floatArrayOf(-y, x, z)

            else ->
                floatArrayOf(x, y, z)
        }
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {
    }

    private fun updateSelectedOrientation() {
        _orientation.value =
            when (currentMethod) {
                MethodType.ACCELEROMETER ->
                    accResult

                MethodType.COMPLEMENTARY ->
                    compResult

                MethodType.MADGWICK ->
                    madgwickResult
            }
    }
}