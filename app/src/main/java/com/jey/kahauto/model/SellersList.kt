package com.jey.kahauto.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sellersListTable")
data class SellersList(
    @PrimaryKey
    @ColumnInfo(name = "listTitle") val listTitle: String,
    @ColumnInfo(name = "participants") var participants: Participants = Participants(arrayListOf()),
    @ColumnInfo(name = "cars") var cars: CarsList = CarsList(arrayListOf())
){
    constructor() : this("",)
}