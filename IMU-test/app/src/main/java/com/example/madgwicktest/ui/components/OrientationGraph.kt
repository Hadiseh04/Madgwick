package com.example.madgwicktest.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp

@Composable
fun OrientationGraph(
    values1: List<Float>,
    values2: List<Float>,
    values3: List<Float>,
    modifier: Modifier = Modifier
) {

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {

        val maxValue = 180f
        val minValue = -180f

        val graphWidth = size.width
        val graphHeight = size.height

        drawRect(
            color = Color.LightGray
        )

        val labels =
            listOf(
                180f,
                90f,
                0f,
                -90f,
                -180f
            )

        labels.forEach { value ->

            val normalized =
                (value - minValue) /
                        (maxValue - minValue)

            val y =
                graphHeight -
                        (normalized * graphHeight)

            drawLine(
                color = Color.LightGray,
                start = Offset(70f, y),
                end = Offset(graphWidth, y),
                strokeWidth = 1f
            )

            drawContext.canvas.nativeCanvas.drawText(
                "${value.toInt()}°",
                10f,
                y + 10f,
                android.graphics.Paint().apply {

                    color =
                        android.graphics.Color.BLACK

                    textSize = 28f
                }
            )
        }


        val sampleRate = 50f

        val totalSeconds =
            values1.size / sampleRate

        val labelCount = 5

        for (i in 0..labelCount) {

            val seconds =
                (totalSeconds / labelCount * i)

            val x =
                70f +
                        (
                                (graphWidth - 100f)
                                        / labelCount
                                        * i
                                )

            drawContext.canvas.nativeCanvas.drawText(
                "${seconds.toInt()}s",
                x,
                graphHeight - 20f,
                android.graphics.Paint().apply {

                    color =
                        android.graphics.Color.BLACK

                    textSize = 24f
                }
            )
        }

        val centerY =
            graphHeight / 2f

        drawLine(
            color = Color.Gray,
            start = Offset(70f, centerY),
            end = Offset(graphWidth, centerY),
            strokeWidth = 3f
        )

        fun drawGraph(
            values: List<Float>,
            color: Color
        ) {

            if (values.size < 2) return

            val widthStep =
                (graphWidth - 70f) /
                        values.size.coerceAtLeast(1)

            for (i in 0 until values.size - 1) {

                val x1 =
                    70f + (i * widthStep)

                val x2 =
                    70f + ((i + 1) * widthStep)

                val y1 =
                    graphHeight -
                            (
                                    (values[i] - minValue)
                                            / (maxValue - minValue)
                                            * graphHeight
                                    )

                val y2 =
                    graphHeight -
                            (
                                    (values[i + 1] - minValue)
                                            / (maxValue - minValue)
                                            * graphHeight
                                    )

                drawLine(
                    color = color,
                    start = Offset(x1, y1),
                    end = Offset(x2, y2),
                    strokeWidth = 4f
                )
            }
        }

        drawGraph(
            values1,
            Color.Red
        )

        drawGraph(
            values2,
            Color.Blue
        )

        drawGraph(
            values3,
            Color.Green
        )
    }
}