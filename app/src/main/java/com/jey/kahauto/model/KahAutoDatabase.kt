package com.jey.kahauto.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database (entities = [SellersList::class, User::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)

abstract class KahAutoDatabase : RoomDatabase() {

    abstract fun getSellersListDao(): SellersListDao

    companion object {
        fun getDatabase(context: Context) : KahAutoDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                KahAutoDatabase::class.java,
                "kah_auto_database"
            ).build()
        }
    }

}