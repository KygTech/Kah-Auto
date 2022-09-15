package com.jey.kahauto

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CarsDao {

    @Insert
    fun insertCar(car:Car)

    @Delete
    fun deleteCar(car:Car)

    @Query("Select * from carsTable")
    fun getAllCars():LiveData<List<Car>>

    @Update
    fun updateCar(car: Car)

    fun updateCarImgUri(car: Car, imagePath:String, imageType: IMAGE_TYPE){
        car.imagePath = imagePath
        car.imageType = imageType
        updateCar(car)
    }

}