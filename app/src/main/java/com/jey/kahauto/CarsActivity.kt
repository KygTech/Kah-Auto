package com.jey.kahauto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView



class CarsActivity : AppCompatActivity() {

    private var carFragment = CarFragment()
    private lateinit var rvCarView: RecyclerView
    private var chosenCar: Car? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rvCarView = findViewById(R.id.rvCarItem)
        val serviceIntent = Intent(this,CarsService::class.java)
        ContextCompat.startForegroundService(this,serviceIntent)
    }

    override fun onStart() {
        super.onStart()
        createRecyclerView()
        btnAddCar()
        removeCarDisplayInfo()
    }

    val getContentFromGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result -> ImagesManager.onImageResultFromGallery(result, chosenCar!!, this) }

//    val getContentFromCamera = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result -> ImagesManager.onImageResultFromCamera(result, chosenCar!!, this) }


    private fun onAddImgClick(): (car: Car) -> Unit = { car ->
        chosenCar = car
        ImagesManager.displayCustomImgDialog(this, car, getContentFromGallery)


    }

    private fun createRecyclerView() {
        val carAdapter = CarAdapter(
            mutableListOf(),
            displayCarInfo(),
            deleteCarItem(),
            onAddImgClick(),
            this
        )
        rvCarView.adapter = carAdapter
        rvCarView.layoutManager = LinearLayoutManager(this)
        val carsListLiveData = Repository.getInstance(this).getAllCars()
        carsListLiveData.observe(this) {
            carAdapter.carsListViewUpdate(it)
        }
    }

    private fun btnAddCar() {
        val btnAdd = findViewById<Button>(R.id.btnAddCar)
        btnAdd.setOnClickListener {
            val intent = Intent(this, CarAddActivity::class.java)
            startActivity(intent)
        }
    }

    private fun deleteCarItem(): (car: Car) -> Unit = {
        Repository.getInstance(this).deleteCar(it)
    }

    private fun displayCarInfo(): (car: Car) -> Unit = {
        val bundle = bundleOf(
            "carCompany" to it.company,
            "carModel" to it.model,
            "carYear" to it.year,
            "carOwners" to it.owners,
            "carKm" to it.carKm,
            "carImgPath" to it.imagePath,
            "carImgType" to it.imageType
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
