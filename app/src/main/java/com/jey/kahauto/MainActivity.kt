package com.jey.kahauto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private var carFragment = CarFragment()
    private lateinit var rvCarView: RecyclerView
    private var chosenCar: Car? = null

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

    val getContent = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
              addImageToCar(uri.toString(),IMAGE_TYPE.URI)
            }
        }

    }

    private fun getImageFromGallery(car: Car) {
        chosenCar = car
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        getContent.launch(intent)
    }

    private fun onAddImgClick(): (car: Car) -> Unit = { car->
        chosenCar = car
//    getImageFromGallery(car)
        getImageFromApi(car)
    }

    private fun getImageFromApi(car: Car) {
        chosenCar = car
        val retrofit = ApiInterface.create()
        retrofit.getImages(car.company).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
               val apiResponse =  response.body()
               val urlImage = apiResponse!!.imagesList[5]
                addImageToCar(urlImage.imageUrl, IMAGE_TYPE.URL)
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.e("Wrong api response", t.message.toString())
            }
        })

    }


    private fun addImageToCar(imagePath: String, imageType: IMAGE_TYPE) {
        thread(start = true) {
            Repository.getInstance(this).updateCarImg(chosenCar!!,imagePath, imageType)
        }
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
