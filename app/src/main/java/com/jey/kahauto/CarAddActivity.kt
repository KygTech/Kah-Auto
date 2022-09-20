package com.jey.kahauto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlin.concurrent.thread

class CarAddActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_add)
    }

    override fun onStart() {
        super.onStart()
        btnAddCar()
        btnCancelForm()
    }

    private fun btnCancelForm() {
        val btnCancelForm = findViewById<Button>(R.id.btnFormCancel)
        btnCancelForm.setOnClickListener {
            clearEtForm()
            val intent = Intent(this, CarsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun addNewCar() :Boolean{
        val formCarCompany = findViewById<EditText>(R.id.carFormCompany).text
        val formCarModel = findViewById<EditText>(R.id.carFormModel).text
        val formCarYear = findViewById<EditText>(R.id.carFormYear).text
        val formCarOwners = findViewById<EditText>(R.id.carFormOwners).text
        val formCarKm = findViewById<EditText>(R.id.carFormKm).text

        if (formCarCompany.isEmpty()) {
            Toast.makeText(this, "Add car company", Toast.LENGTH_SHORT).show()
        } else if (formCarModel.isEmpty()) {
            Toast.makeText(this, "Add car model", Toast.LENGTH_SHORT).show()
        } else if (formCarYear.isEmpty()) {
            Toast.makeText(this, "Add car year", Toast.LENGTH_SHORT).show()
        } else if (formCarOwners.isEmpty()) {
            Toast.makeText(this, "Add car owners", Toast.LENGTH_SHORT).show()
        } else if (formCarKm.isEmpty()) {
            Toast.makeText(this, "Add car km", Toast.LENGTH_SHORT).show()
        } else {
            val car = Car(
                formCarCompany.toString(),
                formCarModel.toString(),
                formCarYear.toString(),
                formCarOwners.toString(),
                formCarKm.toString()
            )
            thread(start = true) {
                Repository.getInstance(this).addCar(car)
            }
            NotificationManager.display(this,car)
            return true
        }
        return false
    }

    private fun btnAddCar() {
        val btnFormDone = findViewById<Button>(R.id.btnFormDone)
        btnFormDone.setOnClickListener {
           if(addNewCar()){
               clearEtForm()
               val intent = Intent(this, CarsActivity::class.java)
               startActivity(intent)
           }else{
               addNewCar()
           }
        }
    }

    private fun clearEtForm() {
        val formCarCompany = findViewById<EditText>(R.id.carFormCompany).text
        val formCarModel = findViewById<EditText>(R.id.carFormModel).text
        val formCarYear = findViewById<EditText>(R.id.carFormYear).text
        val formCarOwners = findViewById<EditText>(R.id.carFormOwners).text
        val formCarKm = findViewById<EditText>(R.id.carFormKm).text

        formCarCompany.clear()
        formCarModel.clear()
        formCarYear.clear()
        formCarOwners.clear()
        formCarKm.clear()

    }


}
