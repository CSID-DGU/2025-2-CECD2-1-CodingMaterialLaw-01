package com.example.iot_air_quality_android.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//    private const val BASE_URL = "http://10.0.2.2:8080" // 애뮬레이터 예시 주소(local)
//    private const val BASE_URL = "http://121.165.157.54:8080" // 실기기 예시 주소(local)
    private const val BASE_URL = "https://www.monodatum.io" // 실제 주소

/**
 * 일반 API용 Retrofit (AuthInterceptor 포함)
 */
object RetrofitInstance {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // 한 번만 생성해서 재사용
    private val authInterceptor = AuthInterceptor()

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)  // 🔐 토큰 붙이는 인터셉터
            .addInterceptor(logging)          // 📜 로깅
            .build()
    }

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

/**
 * 🔁 refresh 전용 Retrofit
 *  - 절대 AuthInterceptor 붙이면 안 됨
 *  - AuthInterceptor 안에서 토큰 재발급할 때 이쪽을 사용
 */
object RetrofitRefreshInstance {

    private val logging = HttpLoggingInterceptor().apply {
        // 필요 없으면 이 로깅은 빼도 됨
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(logging)  // 디버깅용 로깅만
            .build()
    }

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)   // 같은 서버 주소 사용
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
