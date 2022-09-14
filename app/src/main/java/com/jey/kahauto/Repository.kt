package com.jey.kahauto

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData

class Repository private constructor(applicationContext: Context) {

    private val carDao = CarsDatabase.getDatabase(applicationContext).getCarsDao()

    companion object {
        private lateinit var instance:Repository

        fun getInstance(context: Context) : Repository{
            if(!::instance.isInitialized){
                instance = Repository(context)
            }
            return instance
        }
    }

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