package com.example.narzhan.demo

import android.telecom.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by Narzhan on 16/04/2018.
 */
interface ApiaryService{
    @GET()
    fun getGames():retrofit2.Call<Game>

//    @GET("users/{username}")
//    fun getUser(@Path("username") username: String): retrofit2.Call<User>
}

//{
//    [
//    {
//        "name": "Capture the flag",
//        "type": "běhací"
//    }, {
//    "choice": "Bludiště",
//    "votes": "přemýšlecí"
//}, {
//    "choice": "Something",
//    "votes": "something"
//}
//    ]
//}