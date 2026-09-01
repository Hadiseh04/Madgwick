package com.example.madgwicktest.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.madgwicktest.data.model.MethodType
import com.example.madgwicktest.ui.components.MethodSelector
import com.example.madgwicktest.ui.components.ScreenHeader

@Composable
fun SingleMethodSetupScreen(
    onStartClicked: (MethodType) -> Unit
) {
    var selectedMethod by remember {
        mutableStateOf(MethodType.ACCELEROMETER)
    }

    Scaffold(
        topBar = {
            ScreenHeader(
                title = "Single Method"
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(Color.White)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Choose one algorithm to test",
                color = Color.Black
            )

            MethodSelector(
                selectedMethod = selectedMethod,
                onMethodSelected = { method ->
                    selectedMethod = method
                }
            )

            Button(
                onClick = {
                    onStartClicked(selectedMethod)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start test")
            }
        }
    }
}
