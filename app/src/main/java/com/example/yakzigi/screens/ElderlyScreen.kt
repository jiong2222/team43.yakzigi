package com.example.yakzigi.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ElderlyScreen() {

    var message by remember { mutableStateOf("아직 복용 전입니다.") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "오늘 복용할 약",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(30.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("💊 타이레놀", fontSize = 30.sp)
                Spacer(modifier = Modifier.height(15.dp))
                Text("복용일수: 3일", fontSize = 22.sp)
                Text("하루 복용횟수: 하루 3번", fontSize = 22.sp)
                Text("복용 시점: 식후 30분", fontSize = 22.sp)
                Text("복용 시간: 09:00", fontSize = 22.sp)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                message = "복용 완료되었습니다."
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text("복용 완료", fontSize = 24.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = message,
            fontSize = 22.sp
        )
    }
}