package com.example.madgwicktest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun Ball(
    pitch: Float,
    roll: Float,
    yaw: Float,
    color: Color,
    label: String
) {
    var areaSize by remember {
        mutableStateOf(Size.Zero)
    }

    val trail = remember {
        mutableStateListOf<Offset>()
    }

    var initialYaw by remember {
        mutableStateOf<Float?>(null)
    }

    val density = LocalDensity.current
    val ballSizeDp = 50.dp
    val ballSizePx = with(density) {
        ballSizeDp.toPx()
    }
    val sensitivityPx = with(density) {
        6.dp.toPx()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged {
                areaSize = Size(
                    width = it.width.toFloat(),
                    height = it.height.toFloat()
                )
            }
    ) {

        val centerX = areaSize.width / 2f
        val centerY = areaSize.height / 2f

        var x =
            centerX -
                    (ballSizePx / 2f) +
                    (pitch * sensitivityPx)

        var y =
            centerY -
                    (ballSizePx / 2f) -
                    (roll * sensitivityPx)

        x = max(0f, min(x, areaSize.width - ballSizePx))
        y = max(0f, min(y, areaSize.height - ballSizePx))

        if (initialYaw == null) {
            initialYaw = yaw
        }

        val relativeYaw =
            normalizeAngle(
                yaw - (initialYaw ?: yaw)
            )

        LaunchedEffect(x, y) {
            if (areaSize.width > 0f && areaSize.height > 0f) {
                trail.add(
                    Offset(
                        x = x + ballSizePx / 2f,
                        y = y + ballSizePx / 2f
                    )
                )

                while (trail.size > 160) {
                    trail.removeAt(0)
                }
            }
        }

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            if (trail.size > 1) {
                val path = Path()

                trail.forEachIndexed { index, point ->
                    if (index == 0) {
                        path.moveTo(point.x, point.y)
                    } else {
                        path.lineTo(point.x, point.y)
                    }
                }

                drawPath(
                    path = path,
                    color = color.copy(alpha = 0.55f),
                    style = Stroke(
                        width = 10f,
                        cap = StrokeCap.Round
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = x.roundToInt(),
                        y = y.roundToInt()
                    )
                }
                .size(ballSizeDp)
        ) {

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        color = color,
                        shape = CircleShape
                    )
            )

            if (label.isNotBlank()) {
                Text(
                    text = label,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .rotate(-relativeYaw)
            ) {

                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .size(8.dp)
                        .background(
                            color = Color.White,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

private fun normalizeAngle(
    angle: Float
): Float {
    var normalized = angle

    while (normalized > 180f) {
        normalized -= 360f
    }

    while (normalized < -180f) {
        normalized += 360f
    }

    return normalized
}
