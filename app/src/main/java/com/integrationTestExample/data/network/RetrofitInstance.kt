package com.integrationTestExample.data.network

import com.integrationTestExample.utils.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {
        @JvmStatic
        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()

            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)// Adding the interceptor for logging the API details
                .build()
            Retrofit.Builder()
                .baseUrl(BASE_URL) // Adding the base URL
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
        @JvmStatic
        val api by lazy {
            retrofit.create(DrugAPI::class.java)
        }
    }
}