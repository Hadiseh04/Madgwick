package com.example.madgwicktest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.madgwicktest.ui.components.Ball
import com.example.madgwicktest.ui.components.ScreenHeader
import com.example.madgwicktest.ui.viewmodel.MeasurementScreenVM

@Composable
fun ComparisonScreen(
    vm: MeasurementScreenVM
) {

    val accOrientation by vm.accOrientation.collectAsState()
    val compOrientation by vm.compOrientation.collectAsState()
    val madgwickOrientation by vm.madgwickOrientation.collectAsState()

    Scaffold(
        topBar = {
            ScreenHeader(
                title = "Comparison"
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
                pitch = accOrientation.pitch,
                roll = accOrientation.roll,
                yaw = accOrientation.yaw,
                color = Color.Red,
                label = "A"
            )

            Ball(
                pitch = compOrientation.pitch,
                roll = compOrientation.roll,
                yaw = compOrientation.yaw,
                color = Color.Blue,
                label = "C"
            )

            Ball(
                pitch = madgwickOrientation.pitch,
                roll = madgwickOrientation.roll,
                yaw = madgwickOrientation.yaw,
                color = Color.Green,
                label = "M"
            )

            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(24.dp)
            ) {

                Text(
                    text = "Pitch: ${accOrientation.roll}",
                    color = Color.Red
                )

                Text(
                    text = "Roll: ${accOrientation.pitch}",
                    color = Color.Red
                )

                Text(
                    text = "Yaw: ${accOrientation.yaw}",
                    color = Color.Red
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Pitch: ${compOrientation.roll}",
                    color = Color.Blue
                )

                Text(
                    text = "Roll: ${compOrientation.pitch}",
                    color = Color.Blue
                )

                Text(
                    text = "Yaw: ${compOrientation.yaw}",
                    color = Color.Blue
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Pitch: ${madgwickOrientation.roll}",
                    color = Color.Green
                )

                Text(
                    text = "Roll: ${madgwickOrientation.pitch}",
                    color = Color.Green
                )

                Text(
                    text = "Yaw: ${madgwickOrientation.yaw}",
                    color = Color.Green
                )
            }

        }
    }
}
