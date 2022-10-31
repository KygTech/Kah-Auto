package com.jey.kahauto.model

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.jey.kahauto.R

class SharedPManager  private constructor(context: Context) {

    val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(R.string.app_name.toString(), AppCompatActivity.MODE_PRIVATE)

    companion object {
        private lateinit var instance: SharedPManager

        fun getInstance(context: Context): SharedPManager {
            if (!Companion::instance.isInitialized) {
                instance = SharedPManager(context)
            }
            return instance
        }
    }

    fun setMyUser(user: User) {
        sharedPrefs.edit()
            .putString(firstNameKey, user.firstName)
            .putString(lastNameKey, user.lastName)
            .putString(emailKey, user.email)
            .apply()
    }

    fun getMyUser(): User {
        val firstName = sharedPrefs.getString(firstNameKey, "")!!
        val lastName = sharedPrefs.getString(lastNameKey, "")!!
        val email = sharedPrefs.getString(emailKey, "")!!
        return User(email, firstName, lastName)
    }

    fun setLastLogin() {
        sharedPrefs.edit().putLong(lastLoginKey, System.currentTimeMillis()).apply()
    }

    fun getLastLogin(): Long {
        return sharedPrefs.getLong(lastLoginKey, -1)
    }

    private val firstNameKey = "FIRST_NAME"
    private val lastNameKey = "LAST_NAME"
    private val emailKey = "EMAIL"
    private val lastLoginKey = "LAST_LOGIN"

}