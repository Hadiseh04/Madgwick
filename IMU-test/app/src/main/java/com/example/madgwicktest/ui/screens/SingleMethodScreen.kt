package com.example.madgwicktest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.madgwicktest.data.model.MethodType
import com.example.madgwicktest.ui.components.Ball
import com.example.madgwicktest.ui.components.ScreenHeader
import com.example.madgwicktest.ui.viewmodel.MeasurementScreenVM

@Composable
fun SingleMethodScreen(
    viewModel: MeasurementScreenVM
) {

    val orientation by
        viewModel.orientation.collectAsState()

    val selectedMethod by
        viewModel.selectedMethod.collectAsState()

    val title =
        when (selectedMethod) {
            MethodType.ACCELEROMETER -> "Accelerometer"
            MethodType.COMPLEMENTARY -> "Complementary"
            MethodType.MADGWICK -> "Madgwick"
        }

    Scaffold(
        topBar = {
            ScreenHeader(
                title = title
            )
        }
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(Color.White)
        ) {

            Ball(
                pitch = orientation.pitch,
                roll = orientation.roll,
                yaw = orientation.yaw,
                color = Color.Red,
                label = ""
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp)
            ) {

                Text(
                    text = "Pitch: ${orientation.roll}",
                    color = Color.Black
                )

                Text(
                    text = "Roll: ${orientation.pitch}",
                    color = Color.Black
                )

                Text(
                    text = "Yaw: ${orientation.yaw}",
                    color = Color.Black
                )
            }
        }
    }
}
