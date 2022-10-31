package com.jey.kahauto.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jey.kahauto.FirebaseManager
import com.jey.kahauto.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CarsViewModel(val app: Application) : AndroidViewModel(app) {

    private val repository = Repository.getInstance(app.applicationContext)
    val sellersListLiveData: MutableLiveData<SellersList> = MutableLiveData()

    fun getCarsLiveData(sellersList: SellersList): LiveData<CarsList> {
        return repository.getCarsBySellersList(sellersList)
    }

    fun addCar(car: Car) {
        viewModelScope.launch(Dispatchers.IO) {
        repository.addCar(sellersListLiveData.value!!, car) }
        }


    fun deleteCar(car: Car){
        viewModelScope.launch(Dispatchers.IO) {
            sellersListLiveData.value?.let { repository.deleteCar(it, car) }
        }
    }

    fun setCurrentSellerList(sellersList: SellersList) {
        sellersListLiveData.value = sellersList
    }

    suspend fun getSellersListByOwner(sellersListOwner: String): SellersList {
        var sellerList: SellersList? = null
        val work = viewModelScope.launch(Dispatchers.IO) {
            sellerList = repository.getSellersListByOwner(sellersListOwner)
        }
        work.join()
        return sellerList!!
    }

}