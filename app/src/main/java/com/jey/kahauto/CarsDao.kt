package com.jey.kahauto

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CarsDao {

    @Insert
    fun insertCar(car:Car)

    @Delete
    fun deleteCar(car:Car)

    @Query("Select * from carsTable")
    fun getAllCars():LiveData<List<Car>>
}