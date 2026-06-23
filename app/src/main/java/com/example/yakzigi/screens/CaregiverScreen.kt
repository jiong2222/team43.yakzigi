package com.example.yakzigi.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CaregiverScreen() {

    var medicineName by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("") }
    var timing by remember { mutableStateOf("") }
    var alarmTime by remember { mutableStateOf("") }

    var message by remember { mutableStateOf("") }
    var showSampleImage by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "돌봄자",
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                medicineName = "타이레놀"
                duration = "3일"
                frequency = "하루 3번"
                timing = "식후 30분"
                alarmTime = "09:00"
                message = "테스트 OCR 결과가 입력되었습니다."
                showSampleImage = true
            }
        ) {
            Text("사진 선택")
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (showSampleImage) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("선택한 약봉투 이미지")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        OutlinedTextField(
            value = medicineName,
            onValueChange = { medicineName = it },
            label = { Text("약 이름") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = duration,
            onValueChange = { duration = it },
            label = { Text("복용일수") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = frequency,
            onValueChange = { frequency = it },
            label = { Text("하루 복용횟수") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = timing,
            onValueChange = { timing = it },
            label = { Text("복용 시점") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = alarmTime,
            onValueChange = { alarmTime = it },
            label = { Text("복용 알람 시간") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                com.example.yakzigi.MedicineData.name = medicineName
                com.example.yakzigi.MedicineData.duration = duration
                com.example.yakzigi.MedicineData.frequency = frequency
                com.example.yakzigi.MedicineData.timing = timing
                com.example.yakzigi.MedicineData.alarmTime = alarmTime
                com.example.yakzigi.MedicineData.taken = false

                message = "저장되었습니다. 노인용 화면에서 확인할 수 있습니다."
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("저장")
        }

        Spacer(modifier = Modifier.height(15.dp))

        Text(message)
    }
}