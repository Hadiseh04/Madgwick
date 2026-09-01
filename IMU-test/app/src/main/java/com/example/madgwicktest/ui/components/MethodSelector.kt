package com.example.madgwicktest.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.madgwicktest.data.model.MethodType

@Composable
fun MethodSelector(
    selectedMethod: MethodType,
    onMethodSelected: (MethodType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MethodButton(
            text = "Accelerometer + EWMA",
            selected = selectedMethod == MethodType.ACCELEROMETER,
            onClick = {
                onMethodSelected(MethodType.ACCELEROMETER)
            }
        )

        MethodButton(
            text = "Complementary",
            selected = selectedMethod == MethodType.COMPLEMENTARY,
            onClick = {
                onMethodSelected(MethodType.COMPLEMENTARY)
            }
        )

        MethodButton(
            text = "Madgwick MARG",
            selected = selectedMethod == MethodType.MADGWICK,
            onClick = {
                onMethodSelected(MethodType.MADGWICK)
            }
        )
    }
}

@Composable
private fun MethodButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    if (selected) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text)
        }
    }
}
