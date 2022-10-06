package com.jey.kahauto.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UsersDatabase : RoomDatabase() {

    abstract fun getUsersDao(): UsersDao

    companion object {
        fun getDatabase(context: Context) : UsersDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                UsersDatabase::class.java,
                "users_database"
            ).build()
        }
    }

}