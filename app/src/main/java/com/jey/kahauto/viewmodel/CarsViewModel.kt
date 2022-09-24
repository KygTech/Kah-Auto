package com.jey.kahauto.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.jey.kahauto.model.Car
import com.jey.kahauto.model.Repository

class CarsViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = Repository.getInstance(app.applicationContext)

    val carsListLiveData :LiveData<List<Car>> = repository.getAllCarsAsLiveData()


}