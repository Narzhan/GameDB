package com.example.narzhan.demo

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Narzhan on 16/04/2018.
 */

class Api {
    private val url = "https://private-033aff-narzhan.apiary-mock.com/narzhan/test/questions"

    private var retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    fun getGames(): ApiaryService {
        return retrofit.create(ApiaryService::class.java)
    }
}