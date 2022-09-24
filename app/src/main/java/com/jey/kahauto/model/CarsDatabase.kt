package com.jey.kahauto.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database (entities = [Car::class], version = 1, exportSchema = false)
abstract class CarsDatabase : RoomDatabase() {

    abstract fun getCarsDao(): CarsDao

    companion object {
        fun getDatabase(context: Context) : CarsDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                CarsDatabase::class.java,
                "cars_database"
            ).build()
        }
    }

}