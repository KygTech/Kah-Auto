package com.jey.kahauto

import android.app.Application
import androidx.lifecycle.LiveData

class Repository(application: Application) {

    private val carDao = CarsDatabase.getDatabase(application).getCarsDao()

    fun getAllCars(): LiveData<List<Car>>{
        return carDao.getAllCars()
    }

    fun addCar(car: Car){
        carDao.insertCar(car)
    }

    fun deleteCar(car:Car){
        carDao.deleteCar(car)
    }


}