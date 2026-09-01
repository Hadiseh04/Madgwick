package com.example.madgwicktest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.madgwicktest.ui.components.OrientationGraph
import com.example.madgwicktest.ui.components.ScreenHeader
import com.example.madgwicktest.ui.viewmodel.MeasurementScreenVM

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GraphComparisonScreen(
    vm: MeasurementScreenVM
) {

    val accOrientation by
    vm.accOrientation.collectAsState()

    val compOrientation by
    vm.compOrientation.collectAsState()

    val madgwickOrientation by
    vm.madgwickOrientation.collectAsState()

    LaunchedEffect(
        accOrientation,
        compOrientation,
        madgwickOrientation
    ) {

        vm.updateGraphData()
    }

    Scaffold(
        topBar = {
            ScreenHeader(
                title = "Graph Comparison"
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp)
                .background(Color.White)
                .verticalScroll(
                    rememberScrollState()
                )
        ) {

            Text(
                text = "Red = Accelerometer",
                color = Color.Red
            )

            Text(
                text = "Blue = Complementary",
                color = Color.Blue
            )

            Text(
                text = "Green = Madgwick",
                color = Color.Green
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )


            Text(
                text = "Pitch",
                color = Color.Black
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OrientationGraph(
                values1 = vm.accPitch,
                values2 = vm.compPitch,
                values3 = vm.madgwickPitch
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )


            Text(
                text = "Roll",
                color = Color.Black
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OrientationGraph(
                values1 = vm.accRoll,
                values2 = vm.compRoll,
                values3 = vm.madgwickRoll
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Yaw",
                color = Color.Black
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OrientationGraph(
                values1 = vm.accYaw,
                values2 = vm.compYaw,
                values3 = vm.madgwickYaw
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            FlowRow(

                horizontalArrangement =
                    Arrangement.spacedBy(8.dp),

                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                Button(
                    onClick = {
                        vm.toggleRecording()
                    }
                ) {

                    Text(
                        if (vm.recording)
                            "Stop Recording"
                        else
                            "Start Recording"
                    )
                }

                Button(
                    onClick = {
                        vm.resetGraphs()
                    }
                ) {

                    Text("Reset")
                }
            }
        }
    }
}