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

    @Query("UPDATE sellersListTable SET cars=:cars WHERE listTitle = :listTitle")
    fun updateCarsList(listTitle: String, cars: CarsList)

    @Query("Select cars from sellersListTable where listTitle = :listTitle")
    fun getAllCars(listTitle: String): LiveData<CarsList>

    @Query("Select * from sellersListTable where listTitle = :listTitle")
    fun getSellersListByTitle(listTitle: String): SellersList


    @Query("UPDATE sellersListTable SET participants=:participants WHERE listTitle = :listTitle")
    fun updateParticipantsList(listTitle: String, participants: Participants)

    @Query("Select participants from sellersListTable where listTitle = :listTitle")
    fun getAllUsers(listTitle: String): LiveData<Participants>
}