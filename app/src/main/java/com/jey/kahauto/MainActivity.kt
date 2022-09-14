package com.jey.kahauto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private var carFragment = CarFragment()
    private lateinit var rvCarView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rvCarView = findViewById(R.id.rvCarItem)
    }

    override fun onStart() {
        super.onStart()
        createRecyclerView()
        btnAddCar()
        removeCarDisplayInfo()
    }


    private fun createRecyclerView() {
        val carAdapter = CarAdapter(mutableListOf(), { displayCarInfo(it) }, { deleteCarItem(it) })
        rvCarView.adapter = carAdapter
        rvCarView.layoutManager = LinearLayoutManager(this)

        val carsListLiveData = Repository.getInstance(this).getAllCars()
        carsListLiveData.observe(this) {
            carAdapter.carsListViewUpdate(it)
        }
    }

    private fun btnAddCar(){
        val btnAdd = findViewById<Button>(R.id.btnAddCar)
        btnAdd.setOnClickListener {
        val intent = Intent(this, CarAddActivity::class.java)
            startActivity(intent)
        }
    }

//    private fun btnAddCar() {
//        val btnAdd = findViewById<Button>(R.id.btnAddCar)
//        val carCompany = findViewById<EditText>(R.id.et_car_company).text
//        val carModel = findViewById<EditText>(R.id.et_car_model).text
//        val carYear = findViewById<EditText>(R.id.et_car_year).text
//
////        btnAdd.setOnClickListener {
////          if(carCompany.isEmpty()){
////              Toast.makeText(this,"Add car company", Toast.LENGTH_SHORT).show()
////          }else if(carModel.isEmpty()){
////              Toast.makeText(this,"Add car model", Toast.LENGTH_SHORT).show()
////          }else if (carYear.isEmpty()){
////              Toast.makeText(this,"Add car year", Toast.LENGTH_SHORT).show()
////          }else{
////              val car = Car(carCompany.toString(), carModel.toString(), carYear.toString())
////              thread(start = true) {
////                  repository.addCar(car)
////              }
////              carCompany.clear()
////              carModel.clear()
////              carYear.clear()
////          }
////        }
////    }


    private fun deleteCarItem(car: Car) {
        Repository.getInstance(this).deleteCar(car)
    }

    private fun displayCarInfo(car: Car) {
        val bundle = bundleOf(
            "carCompany" to car.company,
            "carModel" to car.model,
            "carYear" to car.year,
            "carOwners" to car.owners,
            "carKm" to car.carKm
        )
        carFragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.car_fragment_view, carFragment)
            .commit()
        rvCarView.isVisible = false
    }

    private fun removeCarDisplayInfo() {
        val carFragmentContainer = findViewById<FragmentContainerView>(R.id.car_fragment_view)
        carFragmentContainer.setOnClickListener {
            supportFragmentManager.beginTransaction().remove(carFragment).commit()
            rvCarView.isVisible = true
        }
    }

}
