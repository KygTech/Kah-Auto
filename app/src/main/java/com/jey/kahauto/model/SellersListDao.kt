package com.jey.kahauto.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SellersListDao {

    @Insert
    fun insertSellerList(sellersList: SellersList)

    @Delete
    fun delete(sellersList: SellersList)

    @Query("Select * from sellersListTable")
    fun getAllSellersLists(): LiveData<List<SellersList>>

    @Query("UPDATE sellersListTable SET carsList=:cars WHERE owner = :owner")
    fun updateCarsList(owner: String, cars: CarsList)

    @Query("Select carsList from sellersListTable where owner = :owner")
    fun getAllCars(owner: String): LiveData<CarsList>

    @Query("Select * from sellersListTable where owner = :owner")
    fun getSellersListByOwner(owner: String): SellersList




}