package com.jey.kahauto.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun toUser(user: String): User {
        val gson = Gson()
        val type = object : TypeToken<User>() {}.type
        return gson.fromJson(user, type)
    }

    @TypeConverter
    fun fromUser(user: User): String {
        val gson = Gson()
        val type = object : TypeToken<User>() {}.type
        return gson.toJson(user, type)
    }

    @TypeConverter
    fun toCar(car: String): Car {
        val gson = Gson()
        val type = object : TypeToken<Car>() {}.type
        return gson.fromJson(car, type)
    }


    @TypeConverter
    fun fromCar(car: Car): String {
        val gson = Gson()
        val type = object : TypeToken<Car>() {}.type
        return gson.toJson(car, type)
    }


    @TypeConverter
    fun toCarsList(cars: String): CarsList {
        val gson = Gson()
        val type = object : TypeToken<CarsList>() {}.type
        return gson.fromJson(cars, type)
    }



    @TypeConverter
    fun fromCarList(cars: CarsList): String {
        val gson = Gson()
        val type = object : TypeToken<CarsList>() {}.type
        return gson.toJson(cars, type)
    }
}