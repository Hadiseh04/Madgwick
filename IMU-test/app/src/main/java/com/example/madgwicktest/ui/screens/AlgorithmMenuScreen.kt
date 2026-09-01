package com.example.madgwicktest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.madgwicktest.ui.components.ScreenHeader

@Composable
fun AlgorithmMenuScreen(
    onSingleMethodClicked: () -> Unit,
    onBallComparisonClicked: () -> Unit,
    onGraphComparisonClicked: () -> Unit
) {
    Scaffold(
        topBar = {
            ScreenHeader(
                title = "Test Algorithms"
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onSingleMethodClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Single Method")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onBallComparisonClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ball Comparison")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onGraphComparisonClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Graph Comparison")
            }
        }
    }
}
