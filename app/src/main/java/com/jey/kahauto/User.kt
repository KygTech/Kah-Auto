package com.jey.kahauto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usersTable" )
data class User (
    @ColumnInfo(name = "username") val username:String,
    @ColumnInfo(name = "password") val password:String,
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "createdAt") var createdAt: Long = System.currentTimeMillis()

)
{
    @PrimaryKey(autoGenerate = true)
    var id = 0
}