package com.example.yakzigi.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun CaregiverScreen() {

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            imageUri = uri
        }

    var medicineName by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("") }
    var timing by remember { mutableStateOf("") }

    var message by remember { mutableStateOf("") }

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
                launcher.launch("image/*")
            }
        ) {
            Text("사진 선택")
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (imageUri != null) {

            AsyncImage(
                model = imageUri,
                contentDescription = null,
                modifier = Modifier
                    .size(220.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        OutlinedTextField(
            value = medicineName,
            onValueChange = { medicineName = it },
            label = { Text("약 이름") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = duration,
            onValueChange = { duration = it },
            label = { Text("복용일수") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = frequency,
            onValueChange = { frequency = it },
            label = { Text("하루 복용횟수") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = timing,
            onValueChange = { timing = it },
            label = { Text("복용 시점") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                message = "저장되었습니다."
            }
        ) {
            Text("저장")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(message)

    }
}