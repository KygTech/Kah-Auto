package com.jey.kahauto.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.jey.kahauto.model.Car
import com.jey.kahauto.model.IMAGE_TYPE
import com.jey.kahauto.model.Repository

class CarsViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = Repository.getInstance(app.applicationContext)

    val carsListLiveData: LiveData<List<Car>> = repository.getAllCarsAsLiveData()

    fun addCar(car: Car) {
        repository.addCar(car)
    }

    fun deleteCar(car: Car) {
        repository.deleteCar(car)
    }

    fun updateCarImg(car: Car, uri: String, imageType: IMAGE_TYPE) {
        repository.updateCarImg(car, uri, imageType)
    }
}