package com.example.yakzigi.screens

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yakzigi.AlarmReceiver
import com.example.yakzigi.Medicine
import com.example.yakzigi.MedicineData
import com.example.yakzigi.OcrResponse
import com.example.yakzigi.RetrofitClient
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun CaregiverScreen() {

    val context = LocalContext.current

    var medicineName by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("") }
    var timing by remember { mutableStateOf("") }
    var alarmTime by remember { mutableStateOf("09:00") }

    var message by remember { mutableStateOf("") }
    var showSampleImage by remember { mutableStateOf(false) }

    fun uploadImageToOcrServer(uri: Uri) {
        message = "OCR 분석 중입니다..."

        val inputStream = context.contentResolver.openInputStream(uri)

        if (inputStream == null) {
            message = "이미지를 불러오지 못했습니다."
            return
        }

        val imageBytes = inputStream.readBytes()
        inputStream.close()

        val requestBody = imageBytes.toRequestBody("image/*".toMediaTypeOrNull())

        val imagePart = MultipartBody.Part.createFormData(
            "file",
            "medicine_image.jpg",
            requestBody
        )

        RetrofitClient.api.uploadImage(imagePart)
            .enqueue(object : Callback<OcrResponse> {
                override fun onResponse(
                    call: Call<OcrResponse>,
                    response: Response<OcrResponse>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        val firstMedicine = result?.medicines?.firstOrNull()

                        if (firstMedicine != null) {
                            medicineName = firstMedicine.name
                            duration = firstMedicine.duration_days.toString()
                            frequency = firstMedicine.times_per_day.toString()
                            timing = firstMedicine.take_timing
                            alarmTime = "09:00"

                            showSampleImage = true
                            message = "OCR 결과가 자동 입력되었습니다."
                        } else {
                            message = "OCR 결과에서 약 정보를 찾지 못했습니다."
                        }
                    } else {
                        message = "OCR 서버 응답 실패: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<OcrResponse>, t: Throwable) {
                    message = "OCR 서버 연결 실패: ${t.message}"
                }
            })
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            showSampleImage = true
            uploadImageToOcrServer(uri)
        } else {
            message = "사진 선택이 취소되었습니다."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "돌봄자", fontSize = 28.sp)

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                imagePickerLauncher.launch("image/*")
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
                val newMedicine = Medicine(
                    name = medicineName,
                    duration_days = duration.toIntOrNull() ?: 0,
                    times_per_day = frequency.toIntOrNull() ?: 0,
                    take_timing = timing,
                    alarm_times = listOf(alarmTime)
                )

                MedicineData.medicines.clear()
                MedicineData.medicines.add(newMedicine)

                val db = FirebaseFirestore.getInstance()

                val medicineMap = hashMapOf(
                    "name" to newMedicine.name,
                    "duration_days" to newMedicine.duration_days,
                    "times_per_day" to newMedicine.times_per_day,
                    "take_timing" to newMedicine.take_timing,
                    "alarm_times" to newMedicine.alarm_times
                )

                db.collection("medications")
                    .add(medicineMap)
                    .addOnSuccessListener {
                        message = "저장되었습니다. Firebase에도 저장되었습니다."
                    }
                    .addOnFailureListener {
                        message = "Firebase 저장에 실패했습니다."
                    }

                val intent = Intent(context, AlarmReceiver::class.java)

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )

                val alarmManager =
                    context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + 10000,
                    pendingIntent
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("저장")
        }

        Spacer(modifier = Modifier.height(15.dp))

        Text(message)
    }
}