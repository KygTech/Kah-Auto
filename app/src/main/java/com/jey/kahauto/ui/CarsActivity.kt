package com.jey.kahauto.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.jey.kahauto.*
import com.jey.kahauto.model.*
import com.jey.kahauto.viewmodel.CarsViewModel
import kotlinx.android.synthetic.main.activity_cars.*
import kotlinx.android.synthetic.main.dialog_add_car.view.*
import kotlinx.android.synthetic.main.dialog_choose_img.view.*

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CarsActivity : AppCompatActivity() {

    private val carsViewModel: CarsViewModel by viewModels()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val sharedPreferences = SharedPManager.getInstance(this)

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

        removeCarDisplayInfo()
        createRecyclerView()

        val sellersListOwner = intent.extras?.get("owner")
        if (sellersListOwner != null) {
            carsViewModel.viewModelScope.launch {
                val sellersList = carsViewModel.getSellersListByOwner(sellersListOwner as String)
                carsViewModel.setCurrentSellerList(sellersList)
                val userName = sellersList.user.userName
                val userEmail = sellersList.user.email

                seller_list_title.text = " $sellersListOwner list By ${userName.lowercase()} "
                onBtnClickAddCar(userEmail)
            }
        }
    }





    private val getContentFromGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val context = this
        carsViewModel.viewModelScope.launch(Dispatchers.IO) {
            ImagesManager.onImageResultFromGallery(
                result,
                chosenCar!!,
                context,
                carsViewModel.sellersListLiveData.value!!
            )
        }
    }


    private val getContentFromCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        ImagesManager.onImageResultFromCamera(
            result,
            chosenCar!!,
            this,
            carsViewModel.sellersListLiveData.value!!
        )
    }


    private fun onAddImgClick(): (car: Car) -> Unit = { car ->
        chosenCar = car
        displayCustomImgDialog(
            car,
            getContentFromGallery,
            getContentFromCamera,
            this,
            carsViewModel.sellersListLiveData.value!!
        )
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

        carsViewModel.sellersListLiveData.observe(this) { sellersList ->
            carsViewModel.getCarsLiveData(sellersList).observe(this) {
                carAdapter.carsListViewUpdate(it.carsList)
            }
        }
    }


    private fun onBtnClickAddCar(userEmail: String) {
        btnAddCar.isVisible = userEmail == sharedPreferences.getMyUser().email
        btnAddCar.setOnClickListener {
            displayAddCarDialog()
        }
    }

    private fun addNewCar(view: View): Boolean {

        val carCompany = view.carFormCompany.text
        val carModel = view.carFormModel.text
        val carYear = view.carFormYear.text
        val carOwners = view.carFormOwners.text
        val carKm = view.carFormKm.text

        if (carCompany.isEmpty()) {
            Toast.makeText(this, "Add car company", Toast.LENGTH_SHORT).show()
        } else if (carModel.isEmpty()) {
            Toast.makeText(this, "Add car model", Toast.LENGTH_SHORT).show()
        } else if (carYear.isEmpty()) {
            Toast.makeText(this, "Add car year", Toast.LENGTH_SHORT).show()
        } else if (carOwners.isEmpty()) {
            Toast.makeText(this, "Add car owners", Toast.LENGTH_SHORT).show()
        } else if (carKm.isEmpty()) {
            Toast.makeText(this, "Add car km", Toast.LENGTH_SHORT).show()
        } else {
            val car = Car(
                carCompany.toString(),
                carModel.toString(),
                carYear.toString(),
                carOwners.toString(),
                carKm.toString()
            )
            carsViewModel.viewModelScope.launch(Dispatchers.IO) {
                carsViewModel.addCar(car)
            }
            return true
        }
        return false
    }

    private fun displayAddCarDialog() {

        val mDialogView = layoutInflater
            .inflate(R.layout.dialog_add_car, null, false)

        val dialog = AlertDialog.Builder(this)
            .setView(mDialogView)
            .create()

        val btnFormDone = mDialogView.btnFormDone
        btnFormDone.setOnClickListener {
            if (addNewCar(mDialogView)) {
                dialog.cancel()
            }

        }

        val btnFormCancel = mDialogView.btnFormCancel
        btnFormCancel.setOnClickListener { dialog.cancel() }
        dialog.show()

    }

    private fun deleteCarItem(): (car: Car) -> Unit = {
        carsViewModel.viewModelScope.launch(Dispatchers.IO) {
            carsViewModel.deleteCar(it)
        }
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

    private fun displayCustomImgDialog(
        car: Car,
        getContentFromGallery: ActivityResultLauncher<Intent>,
        getContentFromCamera: ActivityResultLauncher<Intent>,
        context: Context,
        sellersList: SellersList
    ) {
        carsViewModel.viewModelScope.launch(Dispatchers.Main) {
            val mDialogView = LayoutInflater
                .from(context)
                .inflate(R.layout.dialog_choose_img, null)

            val dialog = AlertDialog.Builder(context)
                .setView(mDialogView)
                .create()

            dialog.show()

            val btnGallery = mDialogView.dialog_img_from_gallery
            btnGallery.setOnClickListener {
                carsViewModel.viewModelScope.launch(Dispatchers.IO) {
                    ImagesManager.getImageFromGallery(car, getContentFromGallery)
                }
                dialog.cancel()

            }

            val btnNetwork = mDialogView.dialog_img_from_network
            btnNetwork.setOnClickListener {
                carsViewModel.viewModelScope.launch(Dispatchers.IO) {
                    ImagesManager.getImageFromApi(
                        car,
                        context,
                        carsViewModel.sellersListLiveData.value!!
                    )
                }
                dialog.cancel()
            }

            val btnCamera = mDialogView.dialog_img_from_camera
            btnCamera.setOnClickListener {
                carsViewModel.viewModelScope.launch(Dispatchers.IO) {
                    ImagesManager.captureImageFromCamera(car, getContentFromCamera)
                }
                dialog.cancel()
            }


            if (car.imagePath == null || car.imagePath == R.drawable.camera_icon_two.toString()) {
                mDialogView.dialog_remove_img.isVisible = false
                mDialogView.dialog_remove_title.isVisible = false
            }

            val btnRemoveImg = mDialogView.dialog_remove_img
            btnRemoveImg.setOnClickListener {
                carsViewModel.viewModelScope.launch(Dispatchers.IO) {
                    ImagesManager.addImageToCar(
                        car,
                        R.drawable.camera_icon_two.toString(),
                        IMAGE_TYPE.URI,
                        context,
                        sellersList
                    )
                }
                dialog.cancel()
            }

            val btnCancel = mDialogView.dialog_cancel
            btnCancel.setOnClickListener { dialog.cancel() }
        }
    }


}

