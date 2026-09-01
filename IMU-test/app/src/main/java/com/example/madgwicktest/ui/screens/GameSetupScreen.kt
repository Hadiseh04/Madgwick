package com.example.madgwicktest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.madgwicktest.data.model.MethodType
import com.example.madgwicktest.ui.components.MethodSelector
import com.example.madgwicktest.ui.components.ScreenHeader
import com.example.madgwicktest.ui.viewmodel.GameViewModel

@Composable
fun GameSetupScreen(
    gameViewModel: GameViewModel,
    onStartClicked: (MethodType, Int) -> Unit
) {
    var selectedMethod by remember {
        mutableStateOf(MethodType.MADGWICK)
    }

    var selectedDuration by remember {
        mutableIntStateOf(60)
    }

    Scaffold(
        topBar = {
            ScreenHeader(
                title = "Game Setup"
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(Color.White)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Choose algorithm",
                color = Color.Black
            )

            MethodSelector(
                selectedMethod = selectedMethod,
                onMethodSelected = {
                    selectedMethod = it
                }
            )

            Text(
                text = "Choose time",
                color = Color.Black
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(30, 60, 90).forEach { seconds ->
                    if (selectedDuration == seconds) {
                        Button(
                            onClick = {
                                selectedDuration = seconds
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("${seconds}s")
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                selectedDuration = seconds
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("${seconds}s")
                        }
                    }
                }
            }

            Text(
                text = "The target sequence is shown as an animation inside the Godot game before the round starts.",
                color = Color.DarkGray
            )

            Button(
                onClick = {
                    onStartClicked(
                        selectedMethod,
                        selectedDuration
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start")
            }
        }
    }
}
