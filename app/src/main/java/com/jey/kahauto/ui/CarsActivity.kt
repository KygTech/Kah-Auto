package com.jey.kahauto.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.jey.kahauto.*
import com.jey.kahauto.model.Car
import com.jey.kahauto.model.Repository
import com.jey.kahauto.viewmodel.CarsViewModel
import kotlinx.android.synthetic.main.activity_cars.*


class CarsActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private val carsViewModel: CarsViewModel by viewModels()
    private val firebaseAuth = FirebaseAuth.getInstance()


    private var carFragment = CarFragment()
    private var chosenCar: Car? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cars)
        val serviceIntent = Intent(this, CarsService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    override fun onStart() {
        super.onStart()
        createRecyclerView()
        btnAddCar()
        removeCarDisplayInfo()
        sharedPreferences = getSharedPreferences(R.string.app_name.toString(), MODE_PRIVATE)

        val username = sharedPreferences.getString("USER_NAME", "")
        hey_user.text = "Hello, $username"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> {
                signOutFromApp()
            }
            R.id.menu_about -> {}
        }
        return super.onOptionsItemSelected(item)
    }


    private fun signOutFromApp() {

        GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut()

        sharedPreferences.edit().remove("LAST_LOGIN").apply()

        firebaseAuth.signOut()
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
        finish()
    }


    private val getContentFromGallery = registerForActivityResult(
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
        car_rv.adapter = carAdapter
        car_rv.layoutManager = LinearLayoutManager(this)

        carsViewModel.carsListLiveData.observe(this) {
            carAdapter.carsListViewUpdate(it)
        }


    }

    private fun btnAddCar() {
        btnAddCar.setOnClickListener {
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
        car_rv.isVisible = false
    }

    private fun removeCarDisplayInfo() {
        car_fragment_view.setOnClickListener {
            supportFragmentManager.beginTransaction().remove(carFragment).commit()
            car_rv.isVisible = true
        }
    }


}
