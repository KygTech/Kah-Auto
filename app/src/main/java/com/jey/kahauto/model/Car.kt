package com.jey.kahauto.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class IMAGE_TYPE {
    URI, URL,
}

@Entity(tableName = "carsTable")
data class Car(
    @ColumnInfo(name = "company") val company:String,
    @ColumnInfo(name = "model") val model: String,
    @ColumnInfo(name = "year") val year: String,
    @ColumnInfo(name = "owners") val owners:String,
    @ColumnInfo(name = "km") val carKm: String,
    @ColumnInfo(name = "image_path") var imagePath : String? = null,
    @ColumnInfo(name = "image_type") var imageType: IMAGE_TYPE?= null,
    @ColumnInfo(name = "timestamp") var timestamp: Long = System.currentTimeMillis()
)
{
    @PrimaryKey(autoGenerate = true)
    var id = 0
    constructor() : this("" ,"","","","")
}
