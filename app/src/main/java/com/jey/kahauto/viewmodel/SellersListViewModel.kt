package com.jey.kahauto.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.jey.kahauto.model.Repository
import com.jey.kahauto.model.SellersList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SellersListViewModel (val app: Application) : AndroidViewModel(app) {

    private val repository = Repository.getInstance(app.applicationContext)

    fun getAllSellersListsAsLiveData(): LiveData<List<SellersList>> {
        return repository.getSellersList()
    }


    fun createSellerList(sellersList: SellersList) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addSellersList(sellersList)
        }
    }


}