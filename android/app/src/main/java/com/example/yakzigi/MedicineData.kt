package com.example.yakzigi

import androidx.compose.runtime.mutableStateListOf

data class Medicine(
    var name: String = "",
    var duration_days: Int = 0,
    var times_per_day: Int = 0,
    var take_timing: String = "",
    var alarm_times: List<String> = listOf()
)

object MedicineData {
    var medicines = mutableStateListOf<Medicine>()
}