package com.nvnrdhn.happyface

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface ApiService {
    companion object {
        private val auth = "0bffebc455d74921aa7a32bfd3968dfc"
        private val baseUrl = "https://fp-ppb.cognitiveservices.azure.com/face/v1.0/"

        private class AuthInterceptor: Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val request = chain.request().newBuilder()
                    .header("Ocp-Apim-Subscription-Key", auth)
                    .header("Connection", "close")
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/octet-stream")
                    .build()
                return chain.proceed(request)
            }
        }

        fun create(): ApiService {
            val httpClient = OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(AuthInterceptor())
                .build()
            val retrofit = Retrofit.Builder()
                .client(httpClient)
                .addConverterFactory(
                    GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }

    @POST("detect?recognitionModel=recognition_02&returnFaceAttributes=age,emotion")
    fun detect(@Body photo: RequestBody): Call<List<Model.FaceData>>
}