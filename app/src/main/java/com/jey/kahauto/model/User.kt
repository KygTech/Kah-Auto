package com.jey.kahauto.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usersTable")
data class User(
    @PrimaryKey
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "firstname") val firstName: String,
    @ColumnInfo(name = "lastname") val lastName: String,
    @ColumnInfo(name = "phoneNumber") val phoneNumber: String = "0505556666",
    @ColumnInfo(name = "image_path") var imagePath : String? = null,
    @ColumnInfo(name = "image_type") var imageType: IMAGE_TYPE?= null,
    @ColumnInfo(name = "createdAt") var createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "createdBy") var createdBy: String = "Kah-Auto SignIn",
    @ColumnInfo(name = "username") var userName: String = "$firstName $lastName"
)
{
    constructor() : this("","","")
}