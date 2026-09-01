package com.example.madgwicktest.data.filter

import com.example.madgwicktest.data.model.Orientation
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.sqrt

class MadgwickFilter(
    private val beta: Float = 0.05f
) {

    private var q = Quaternion(
        w = 1f,
        x = 0f,
        y = 0f,
        z = 0f
    )

    fun update(
        ax: Float,
        ay: Float,
        az: Float,
        gx: Float,
        gy: Float,
        gz: Float,
        mx: Float,
        my: Float,
        mz: Float,
        dt: Float
    ): Orientation {

        if (dt <= 0f) {
            return q.toEuler()
        }

        val accelerometer =
            Vector3(
                x = ax,
                y = ay,
                z = az
            ).normalizedOrNull()
                ?: return q.toEuler()

        val magnetometer =
            Vector3(
                x = mx,
                y = my,
                z = mz
            ).normalizedOrNull()

        val gyroDerivative =
            q.gyroscopeDerivative(
                gx = gx,
                gy = gy,
                gz = gz
            )

        val gradient =
            if (magnetometer == null) {
                imuGradient(
                    q = q,
                    a = accelerometer
                )
            } else {
                margGradient(
                    q = q,
                    a = accelerometer,
                    m = magnetometer
                )
            }.normalizedOrZero()

        val correctedDerivative =
            gyroDerivative - (gradient * beta)

        q =
            (q + correctedDerivative * dt)
                .normalizedOrIdentity()

        return q.toEuler()
    }

    private fun imuGradient(
        q: Quaternion,
        a: Vector3
    ): Quaternion {

        val q0 = q.w
        val q1 = q.x
        val q2 = q.y
        val q3 = q.z

        val twoQ0 = 2f * q0
        val twoQ1 = 2f * q1
        val twoQ2 = 2f * q2
        val twoQ3 = 2f * q3
        val fourQ0 = 4f * q0
        val fourQ1 = 4f * q1
        val fourQ2 = 4f * q2
        val eightQ1 = 8f * q1
        val eightQ2 = 8f * q2

        val q0q0 = q0 * q0
        val q1q1 = q1 * q1
        val q2q2 = q2 * q2
        val q3q3 = q3 * q3

        return Quaternion(
            w = fourQ0 * q2q2 +
                    twoQ2 * a.x +
                    fourQ0 * q1q1 -
                    twoQ1 * a.y,

            x = fourQ1 * q3q3 -
                    twoQ3 * a.x +
                    4f * q0q0 * q1 -
                    twoQ0 * a.y -
                    fourQ1 +
                    eightQ1 * q1q1 +
                    eightQ1 * q2q2 +
                    fourQ1 * a.z,

            y = 4f * q0q0 * q2 +
                    twoQ0 * a.x +
                    fourQ2 * q3q3 -
                    twoQ3 * a.y -
                    fourQ2 +
                    eightQ2 * q1q1 +
                    eightQ2 * q2q2 +
                    fourQ2 * a.z,

            z = 4f * q1q1 * q3 -
                    twoQ1 * a.x +
                    4f * q2q2 * q3 -
                    twoQ2 * a.y
        )
    }

    private fun margGradient(
        q: Quaternion,
        a: Vector3,
        m: Vector3
    ): Quaternion {

        val q0 = q.w
        val q1 = q.x
        val q2 = q.y
        val q3 = q.z

        val twoQ0mx = 2f * q0 * m.x
        val twoQ0my = 2f * q0 * m.y
        val twoQ0mz = 2f * q0 * m.z
        val twoQ1mx = 2f * q1 * m.x

        val twoQ0 = 2f * q0
        val twoQ1 = 2f * q1
        val twoQ2 = 2f * q2
        val twoQ3 = 2f * q3
        val twoQ0Q2 = 2f * q0 * q2
        val twoQ2Q3 = 2f * q2 * q3

        val q0q0 = q0 * q0
        val q0q1 = q0 * q1
        val q0q2 = q0 * q2
        val q0q3 = q0 * q3
        val q1q1 = q1 * q1
        val q1q2 = q1 * q2
        val q1q3 = q1 * q3
        val q2q2 = q2 * q2
        val q2q3 = q2 * q3
        val q3q3 = q3 * q3

        val hx =
            m.x * q0q0 -
                    twoQ0my * q3 +
                    twoQ0mz * q2 +
                    m.x * q1q1 +
                    twoQ1 * m.y * q2 +
                    twoQ1 * m.z * q3 -
                    m.x * q2q2 -
                    m.x * q3q3

        val hy =
            twoQ0mx * q3 +
                    m.y * q0q0 -
                    twoQ0mz * q1 +
                    twoQ1mx * q2 -
                    m.y * q1q1 +
                    m.y * q2q2 +
                    twoQ2 * m.z * q3 -
                    m.y * q3q3

        val twoBx =
            sqrt(hx * hx + hy * hy)

        val twoBz =
            -twoQ0mx * q2 +
                    twoQ0my * q1 +
                    m.z * q0q0 +
                    twoQ1mx * q3 -
                    m.z * q1q1 +
                    twoQ2 * m.y * q3 -
                    m.z * q2q2 +
                    m.z * q3q3

        val fourBx = 2f * twoBx
        val fourBz = 2f * twoBz

        val f1 =
            2f * q1q3 -
                    twoQ0Q2 -
                    a.x

        val f2 =
            2f * q0q1 +
                    twoQ2Q3 -
                    a.y

        val f3 =
            1f -
                    2f * q1q1 -
                    2f * q2q2 -
                    a.z

        val f4 =
            twoBx * (0.5f - q2q2 - q3q3) +
                    twoBz * (q1q3 - q0q2) -
                    m.x

        val f5 =
            twoBx * (q1q2 - q0q3) +
                    twoBz * (q0q1 + q2q3) -
                    m.y

        val f6 =
            twoBx * (q0q2 + q1q3) +
                    twoBz * (0.5f - q1q1 - q2q2) -
                    m.z

        return Quaternion(
            w = -twoQ2 * f1 +
                    twoQ1 * f2 -
                    twoBz * q2 * f4 +
                    (-twoBx * q3 + twoBz * q1) * f5 +
                    twoBx * q2 * f6,

            x = twoQ3 * f1 +
                    twoQ0 * f2 -
                    4f * q1 * f3 +
                    twoBz * q3 * f4 +
                    (twoBx * q2 + twoBz * q0) * f5 +
                    (twoBx * q3 - fourBz * q1) * f6,

            y = -twoQ0 * f1 +
                    twoQ3 * f2 -
                    4f * q2 * f3 +
                    (-fourBx * q2 - twoBz * q0) * f4 +
                    (twoBx * q1 + twoBz * q3) * f5 +
                    (twoBx * q0 - fourBz * q2) * f6,

            z = twoQ1 * f1 +
                    twoQ2 * f2 +
                    (-fourBx * q3 + twoBz * q1) * f4 +
                    (-twoBx * q0 + twoBz * q2) * f5 +
                    twoBx * q1 * f6
        )
    }

    private data class Vector3(
        val x: Float,
        val y: Float,
        val z: Float
    ) {
        fun normalizedOrNull(): Vector3? {
            val norm =
                sqrt(x * x + y * y + z * z)

            if (norm == 0f) {
                return null
            }

            return Vector3(
                x = x / norm,
                y = y / norm,
                z = z / norm
            )
        }
    }

    private data class Quaternion(
        val w: Float,
        val x: Float,
        val y: Float,
        val z: Float
    ) {
        operator fun plus(
            other: Quaternion
        ): Quaternion =
            Quaternion(
                w = w + other.w,
                x = x + other.x,
                y = y + other.y,
                z = z + other.z
            )

        operator fun minus(
            other: Quaternion
        ): Quaternion =
            Quaternion(
                w = w - other.w,
                x = x - other.x,
                y = y - other.y,
                z = z - other.z
            )

        operator fun times(
            value: Float
        ): Quaternion =
            Quaternion(
                w = w * value,
                x = x * value,
                y = y * value,
                z = z * value
            )

        fun gyroscopeDerivative(
            gx: Float,
            gy: Float,
            gz: Float
        ): Quaternion =
            Quaternion(
                w = 0.5f * (-x * gx - y * gy - z * gz),
                x = 0.5f * (w * gx + y * gz - z * gy),
                y = 0.5f * (w * gy - x * gz + z * gx),
                z = 0.5f * (w * gz + x * gy - y * gx)
            )

        fun normalizedOrZero(): Quaternion {
            val norm =
                sqrt(w * w + x * x + y * y + z * z)

            if (norm == 0f) {
                return Quaternion(
                    w = 0f,
                    x = 0f,
                    y = 0f,
                    z = 0f
                )
            }

            return Quaternion(
                w = w / norm,
                x = x / norm,
                y = y / norm,
                z = z / norm
            )
        }

        fun normalizedOrIdentity(): Quaternion {
            val norm =
                sqrt(w * w + x * x + y * y + z * z)

            if (norm == 0f) {
                return Quaternion(
                    w = 1f,
                    x = 0f,
                    y = 0f,
                    z = 0f
                )
            }

            return Quaternion(
                w = w / norm,
                x = x / norm,
                y = y / norm,
                z = z / norm
            )
        }

        fun toEuler(): Orientation {
            val roll =
                atan2(
                    2.0 * (w * x + y * z),
                    1.0 - 2.0 * (x * x + y * y)
                )

            val pitch =
                asin(
                    (2.0 * (w * y - z * x))
                        .coerceIn(-1.0, 1.0)
                )

            val yaw =
                atan2(
                    2.0 * (w * z + x * y),
                    1.0 - 2.0 * (y * y + z * z)
                )

            return Orientation(
                pitch = Math.toDegrees(pitch).toFloat(),
                roll = Math.toDegrees(roll).toFloat(),
                yaw = Math.toDegrees(yaw).toFloat()
            )
        }
    }
}
