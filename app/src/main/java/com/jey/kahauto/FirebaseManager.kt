package com.jey.kahauto

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.jey.kahauto.model.Car
import com.jey.kahauto.model.SellersList
import com.jey.kahauto.model.User


class FirebaseManager private constructor(context: Context) {
    val db = Firebase.firestore
    val storage = FirebaseStorage.getInstance()

    companion object {
        private lateinit var instance: FirebaseManager

        fun getInstance(context: Context): FirebaseManager {
            if (!Companion::instance.isInitialized) {
                instance = FirebaseManager(context)
            }
            return instance
        }
    }


    fun getUser(userEmail: String): Task<DocumentSnapshot> {
        return db.collection("users").document(userEmail).get()
    }


    fun addUser(newUser: User): Task<Void> {
        return db.collection("users").document(newUser.email).set(newUser)
    }



    fun updateSellersList(sellersList: SellersList) {
        db.collection("sellersList").document(sellersList.listTitle).set(sellersList)
    }


}