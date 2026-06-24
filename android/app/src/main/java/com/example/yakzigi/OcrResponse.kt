package com.example.yakzigi

data class OcrResponse(
    val total_medicines_count: Int,
    val medicines: List<OcrMedicine>
)

data class OcrMedicine(
    val name: String,
    val duration_days: Int,
    val times_per_day: Int,
    val take_timing: String
)