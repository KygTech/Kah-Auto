package com.jey.kahauto.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.jey.kahauto.model.Repository
import com.jey.kahauto.model.User

class UsersViewModel (app: Application) : AndroidViewModel(app){

    private val repository = Repository.getInstance(app.applicationContext)

    fun addUser(user: User) {
        repository.addUser(user)
    }

    val userList = repository.getAllUsers()

}
