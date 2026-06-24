package com.example.yakzigi

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface OcrApi {
    @Multipart
    @POST("ocr/parse")
    fun uploadImage(
        @Header("ngrok-skip-browser-warning") skipWarning: String = "true",
        @Part file: MultipartBody.Part
    ): Call<OcrResponse>
}