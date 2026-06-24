package com.example.yakzigi.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

@Composable
fun PairingScreen() {

    var familyCode by remember { mutableStateOf("") }
    var inputCode by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val uid = auth.currentUser?.uid ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("가족 페어링", fontSize = 32.sp)

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                familyCode = "FAMILY" + Random.nextInt(1000, 9999)

                val familyMap = hashMapOf(
                    "family_id" to familyCode,
                    "caregiver_uid" to uid
                )

                db.collection("families")
                    .document(familyCode)
                    .set(familyMap)
                    .addOnSuccessListener {
                        message = "가족 코드가 생성되었습니다."
                    }
                    .addOnFailureListener {
                        message = "가족 코드 생성 실패"
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("돌봄자: 가족 코드 생성")
        }

        Spacer(modifier = Modifier.height(15.dp))

        Text("생성된 코드: $familyCode")

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = inputCode,
            onValueChange = { inputCode = it },
            label = { Text("노인: 가족 코드 입력") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = {
                if (inputCode.isBlank()) {
                    message = "가족 코드를 입력하세요."
                    return@Button
                }

                db.collection("users")
                    .document(uid)
                    .update("family_id", inputCode)
                    .addOnSuccessListener {
                        message = "가족 페어링이 완료되었습니다."
                    }
                    .addOnFailureListener {
                        message = "가족 페어링 실패"
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("가족 코드로 연결")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(message)
    }
}