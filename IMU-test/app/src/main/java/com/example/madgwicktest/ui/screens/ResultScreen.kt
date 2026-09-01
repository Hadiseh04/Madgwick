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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.madgwicktest.data.model.MethodType
import com.example.madgwicktest.ui.components.ScreenHeader
import com.example.madgwicktest.ui.viewmodel.GameViewModel

@Composable
fun ResultScreen(
    gameViewModel: GameViewModel,
    onTryAgainClicked: () -> Unit,
    onHomeClicked: () -> Unit
) {
    val gameState by
        gameViewModel.gameState.collectAsState()

    Scaffold(
        topBar = {
            ScreenHeader(
                title = "Result"
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(Color.White)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Orientation Match completed",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            ResultRow(
                label = "Algorithm",
                value = gameState.settings.method.displayName()
            )

            ResultRow(
                label = "Time",
                value = "${gameState.settings.durationSeconds}s"
            )

            ResultRow(
                label = "Score",
                value = "${gameState.score} matches"
            )

            ResultRow(
                label = "Target sequence",
                value = "${gameState.targets.size} targets repeated"
            )

            ResultRow(
                label = "Final error",
                value = "${"%.1f".format(gameState.currentError)} deg"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onHomeClicked,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Home")
                }

                Button(
                    onClick = onTryAgainClicked,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Try again")
                }
            }
        }
    }
}

@Composable
private fun ResultRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color.DarkGray
        )

        Text(
            text = value,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun MethodType.displayName(): String =
    when (this) {
        MethodType.ACCELEROMETER -> "Accelerometer + EWMA"
        MethodType.COMPLEMENTARY -> "Complementary filter"
        MethodType.MADGWICK -> "Madgwick MARG"
    }
