package com.jey.kahauto.model

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import com.jey.kahauto.FirebaseManager

class Repository private constructor(applicationContext: Context) {

    private val sellersListDao = KahAutoDatabase.getDatabase(applicationContext).getSellersListDao()
    private val firebaseManager = FirebaseManager.getInstance(applicationContext)

    companion object {
        private lateinit var instance: Repository

        fun getInstance(context: Context): Repository {
            if (!Companion::instance.isInitialized) {
                instance = Repository(context)
            }
            return instance
        }
    }

    fun addCar(sellersList: SellersList, car: Car) {

        sellersList.cars.carsList.add(car)
        firebaseManager.updateSellersList(sellersList)
        sellersListDao.updateCarsList(sellersList.owner, sellersList.cars)
    }

    fun deleteCar(sellersList: SellersList, car: Car) {
        sellersList.cars.carsList.remove(car)
        sellersListDao.updateCarsList(sellersList.owner, sellersList.cars)

    }

    fun updateCarImg(sellersList: SellersList) {
        sellersListDao.updateCarsList(sellersList.owner, sellersList.cars)
        firebaseManager.updateSellersList(sellersList)

    }

    fun getSellersList(): LiveData<List<SellersList>> {
        return sellersListDao.getAllSellersLists()
    }

    fun addSellersList(sellersList: SellersList) {
        firebaseManager.updateSellersList(sellersList)
        return sellersListDao.insertSellerList(sellersList)
    }

    fun getCarsBySellersList(sellersList: SellersList): LiveData<CarsList> {
        return sellersListDao.getAllCars(sellersList.owner)
    }

    fun getSellersListByOwner(sellersListOwner: String): SellersList {
        return sellersListDao.getSellersListByOwner(sellersListOwner)
    }




}