package com.example.yakzigi.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    onCaregiverClick: () -> Unit,
    onElderlyClick: () -> Unit,
    onPairingClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "약지기",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(50.dp))

        Button(onClick = onCaregiverClick) {
            Text("돌봄자")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = onElderlyClick) {
            Text("노인")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = onPairingClick) {
            Text("가족 페어링")
        }
    }
}