package com.jey.kahauto

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiInterface {

    @GET("?key=29930389-7d6df3477b37b99dc4183fa2d&image_type=photo")
    fun getImages(@Query("q") carCompany:String): Call<ApiResponse>

    companion object{
        val BASE_URL = "https://pixabay.com/api/"
        fun create():ApiInterface{
            val retrofit = Retrofit
                .Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(ApiInterface::class.java)
        }
    }


}