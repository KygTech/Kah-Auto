package com.jey.kahauto.model

import android.content.Context
import androidx.lifecycle.LiveData

class Repository private constructor(applicationContext: Context) {

    private val carDao = CarsDatabase.getDatabase(applicationContext).getCarsDao()
    private val userDao = UsersDatabase.getDatabase(applicationContext).getUsersDao()

    companion object {
        private lateinit var instance: Repository

        fun getInstance(context: Context): Repository {
            if (!Companion::instance.isInitialized) {
                instance = Repository(context)
            }
            return instance
        }
    }


    fun getAllCarsAsLiveData(): LiveData<List<Car>> {
        return carDao.getAllCars()
    }

    fun addCar(car: Car) {
        carDao.insertCar(car)
    }

    fun deleteCar(car: Car) {
        carDao.deleteCar(car)
    }

    fun updateCarImg(car: Car, uri: String, imageType: IMAGE_TYPE) {
        carDao.updateCarImgUri(car, uri, imageType)
    }


    fun getAllUsers() : List<User>{
        return userDao.getAllUsers()
    }

    fun addUser(user: User){
        userDao.insertUser(user)
    }

    fun deleteUser(user: User){
        userDao.deleteUser(user)
    }
}