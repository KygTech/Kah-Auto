package com.jey.kahauto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carsTable")
data class Car(
    @ColumnInfo(name = "company") val company:String,
    @ColumnInfo(name = "model") val model: String,
    @ColumnInfo(name = "year") val year: String
            )
{
    @PrimaryKey(autoGenerate = true)
    var id = 0
}

