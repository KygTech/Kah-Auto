package com.jey.kahauto.model

data class Participants(
    val usersList: ArrayList<User>
){
    constructor():this(arrayListOf())
}
