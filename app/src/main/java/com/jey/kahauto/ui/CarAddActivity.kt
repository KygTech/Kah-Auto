package com.jey.kahauto.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.jey.kahauto.model.Car
import com.jey.kahauto.NotificationManager
import com.jey.kahauto.R
import com.jey.kahauto.model.Repository
import kotlinx.android.synthetic.main.activity_car_add.*
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
        btnFormCancel.setOnClickListener {
            clearEtForm()
            val intent = Intent(this, CarsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun addNewCar(): Boolean {

        if (carFormCompany.text.isEmpty()) {
            Toast.makeText(this, "Add car company", Toast.LENGTH_SHORT).show()
        } else if (carFormModel.text.isEmpty()) {
            Toast.makeText(this, "Add car model", Toast.LENGTH_SHORT).show()
        } else if (carFormYear.text.isEmpty()) {
            Toast.makeText(this, "Add car year", Toast.LENGTH_SHORT).show()
        } else if (carFormOwners.text.isEmpty()) {
            Toast.makeText(this, "Add car owners", Toast.LENGTH_SHORT).show()
        } else if (carFormKm.text.isEmpty()) {
            Toast.makeText(this, "Add car km", Toast.LENGTH_SHORT).show()
        } else {
            val car = Car(
                carFormKm.text.toString(),
                carFormModel.text.toString(),
                carFormYear.text.toString(),
                carFormOwners.text.toString(),
                carFormKm.text.toString()
            )
            thread(start = true) {
                Repository.getInstance(this).addCar(car)
            }
            NotificationManager.display(this, car)
            return true
        }
        return false
    }

    private fun btnAddCar() {
        btnFormDone.setOnClickListener {
            if (addNewCar()) {
                clearEtForm()
                val intent = Intent(this, CarsActivity::class.java)
                startActivity(intent)
            } else {
                addNewCar()
            }
        }
    }

    private fun clearEtForm() {
        carFormCompany.text.clear()
        carFormModel.text.clear()
        carFormYear.text.clear()
        carFormOwners.text.clear()
        carFormKm.text.clear()
    }


}
