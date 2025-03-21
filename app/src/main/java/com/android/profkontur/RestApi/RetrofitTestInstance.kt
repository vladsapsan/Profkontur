package com.android.profkontur.RestApi

import com.android.profkontur.Model.Scales
import com.android.profkontur.Model.ScalesDeserializer
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitTestInstance {
    private const val BASE_URL = "https://profkontur.com/rest/"

    //Регистрация gson билдера для десиреализации sealed sсales класса
    private val gson = GsonBuilder()
        .registerTypeAdapter(Scales::class.java, ScalesDeserializer())
        .create()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}