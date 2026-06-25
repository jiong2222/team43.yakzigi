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
        @Part file: MultipartBody.Part,
        @Header("ngrok-skip-browser-warning") skipWarning: String = "true"
    ): Call<OcrResponse>
}