package com.jey.kahauto.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sellersListTable")
data class SellersList(
    @PrimaryKey
    @ColumnInfo(name = "owner") val owner: String,
    @ColumnInfo(name = "user") var user: User,
    @ColumnInfo(name = "carsList") var cars: CarsList = CarsList(arrayListOf())
){
    constructor() : this("", User() )
}