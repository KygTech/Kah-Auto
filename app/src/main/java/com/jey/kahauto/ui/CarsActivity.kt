package com.jey.kahauto.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.jey.kahauto.*
import com.jey.kahauto.model.*
import com.jey.kahauto.viewmodel.CarsViewModel
import kotlinx.android.synthetic.main.activity_cars.*
import kotlinx.android.synthetic.main.activity_sellers.*
import kotlinx.android.synthetic.main.contact_row.view.*
import kotlinx.android.synthetic.main.dialog_add_car.view.*
import kotlinx.android.synthetic.main.dialog_add_user.*
import kotlinx.android.synthetic.main.dialog_add_user.view.*
import kotlinx.android.synthetic.main.dialog_choose_img.view.*

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch


class CarsActivity : BaseActivity() {

    private val carsViewModel: CarsViewModel by viewModels()
    private val sharedPreferences = SharedPManager.getInstance(this)
    private val contactsList = ArrayList<Contact>()
    private lateinit var contactAdapter: ContactAdapter

    private var carFragment = CarFragment()
    private var participantFragment = ParticipantFragment()
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
        removeParticipantDisplayInfo()
        createParticipantsRecyclerView()

        val sellerListTitle = intent.extras?.get("listTitle")
        if (sellerListTitle != null) {
            carsViewModel.viewModelScope.launch {
                val sellersList = carsViewModel.getSellersListByTitle(sellerListTitle as String)
                carsViewModel.setCurrentSellerList(sellersList)

                val participantsList = sellersList.participants.usersList
                createCarsListRecyclerView(participantsList)
                seller_list_title.text = " ${sellerListTitle.uppercase()}  "
                onBtnClickAddCar()
                if (!checkCurrentParticipants(participantsList))
                    closeButtonsVisible()
            }
        }
    }

    private fun closeButtonsVisible() {
        add_user_iv.isVisible = false
        btnAddCar.isVisible = false
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

    private fun createParticipantsRecyclerView() {
        val participantsAdapter = ParticipantsAdapter(this, arrayListOf(), displayParticipantInfo())

        participants_rv.adapter = participantsAdapter
        participants_rv.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        carsViewModel.sellersListLiveData.observe(this) { sellerList ->
            carsViewModel.getParticipantLiveData(sellerList).observe(this) { participant ->
                participantsAdapter.participantsListViewUpdate(participant.usersList)
            }
        }
    }

    private fun displayParticipantInfo(): (user: User) -> Unit = {
        val bundle = bundleOf(
            "participant_first_name" to it.firstName,
            "participant_last_name" to it.lastName,
            "participant_email" to it.email,
            "participant_phone_number" to it.phoneNumber,
            "participant_img_path" to it.imagePath,
            "participant_img_type" to it.imageType
        )

        participantFragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.container_fragment_view, participantFragment)
            .commit()
        car_rv.isVisible = false
    }


    private fun createCarsListRecyclerView(participantsList: ArrayList<User>) {
        val carAdapter = CarAdapter(
            arrayListOf(),
            displayCarInfo(),
            deleteCarItem(),
            onAddImgClick(),
            this,
            checkCurrentParticipants(participantsList)
        )
        car_rv.adapter = carAdapter
        car_rv.layoutManager = LinearLayoutManager(this)

        carsViewModel.sellersListLiveData.observe(this) { sellersList ->
            carsViewModel.getCarsLiveData(sellersList).observe(this) {
                carAdapter.carsListViewUpdate(it.carsList)
            }
        }
    }

    private fun checkCurrentParticipants(participantsList: ArrayList<User>): Boolean {
        participantsList.forEach {
            if (it.email == sharedPreferences.getMyUser().email) {
                return true
            }
        }
        return false
    }

    private fun onBtnClickAddCar() {
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
            .replace(R.id.container_fragment_view, carFragment)
            .commit()
        car_rv.isVisible = false
    }

    private fun removeCarDisplayInfo() {
        container_fragment_view.setOnClickListener {
            supportFragmentManager.beginTransaction().remove(carFragment).commit()
            car_rv.isVisible = true
        }
    }

    private fun removeParticipantDisplayInfo() {
        container_fragment_view.setOnClickListener {
            supportFragmentManager.beginTransaction().remove(participantFragment).commit()
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


    private fun checkParticipantsInSellsList(
        participants: Participants,
        userEmail: String
    ): Boolean {
        for (user in participants.usersList) {
            if (user.email == userEmail) {
                return true
            }
        }
        return false
    }


    fun addUserOnClick(view: View) {
        displayAddUserAlertDialog()
    }


    private fun displayAddUserAlertDialog() {

        val mDialogView = layoutInflater
            .inflate(R.layout.dialog_add_user, null, false)


        val dialog = AlertDialog.Builder(this)
            .setView(mDialogView)
            .create()

        checkPermission()

        val btnSearchByEmail = mDialogView.search_user_btn
        btnSearchByEmail.setOnClickListener {
            val userEmail = mDialogView.search_by_email_ed.text.toString()
            if (userEmail.isNotEmpty()) {
                dialog.cancel()
                val participantsList =
                    carsViewModel.sellersListLiveData.value!!.participants

                if (checkParticipantsInSellsList(participantsList, userEmail)) {
                    Toast.makeText(
                        this,
                        "User is already participant in the list",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    carsViewModel.viewModelScope.launch {
                        carsViewModel.addUser(applicationContext, userEmail)

                    }
                }
            } else {
                Toast.makeText(applicationContext, "Email field is empty", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        val closeAddUser = mDialogView.close_add_user
        closeAddUser.setOnClickListener {
            dialog.cancel()
        }


        mDialogView.contact_sv.clearFocus()
        mDialogView.contact_sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                fillerList(newText)
                return true
            }
        })




        mDialogView.contact_rv.layoutManager = LinearLayoutManager(this)
        contactAdapter = ContactAdapter(contactsList, onContactClick(), this)
        mDialogView.contact_rv.adapter = contactAdapter




        dialog.show()


    }

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getAllPhoneContacts()
            }
        }

    private fun checkPermission() {
        val isPermissionAlreadyGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CONTACTS
        )
        if (isPermissionAlreadyGranted == PackageManager.PERMISSION_GRANTED) {
            getAllPhoneContacts()
        } else {
            val needToExplainThePermission =
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_CONTACTS
                )
            if (needToExplainThePermission) {
                Toast.makeText(
                    this,
                    "We need your permission to find contact for you",
                    Toast.LENGTH_SHORT
                ).show()
                requestPermission.launch(Manifest.permission.READ_CONTACTS)
            } else {
                requestPermission.launch(Manifest.permission.READ_CONTACTS)
            }
        }

    }


    @SuppressLint("Range")
    fun getAllPhoneContacts() {

        var cols = listOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
        ).toTypedArray()

        val rs = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            cols,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        )

        while (rs!!.moveToNext()) {
            val name =
                rs.getString(rs.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phone =
                rs.getString(rs.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            val newContact = Contact(name, phone)
            contactsList.add(newContact)
        }

    }

    private fun fillerList(text: String) {
        val filteredList = ArrayList<Contact>()
        for (contact in contactsList) {
            if (contact.displayName.lowercase().contains(text.lowercase())) {
                filteredList.add(contact)
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No contact found", Toast.LENGTH_SHORT).show()
            filteredList.removeAll(filteredList.toSet())
            contactAdapter.setFilteredList(filteredList)
        } else {
            contactAdapter.setFilteredList(filteredList)
        }
    }


    private fun onContactClick(): (Contact) -> Unit = {

        sendInviteByWhatsApp(it.phoneNumber)


//        val phone = it.phoneNumber
//        val display = it.displayName
//        Toast.makeText(this, "$display -  $phone", Toast.LENGTH_SHORT).show()
//        carsViewModel.viewModelScope.launch {
//            carsViewModel.getAllUsers().forEach {
//               Log.d ("Test" ,"$it.email")
//            }
//            }
    }


    private fun appInstalledOrNot(url: String): Boolean {
        val appInstalled: Boolean = try {
            packageManager.getPackageInfo(url, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        return appInstalled
    }

    private fun sendInviteByWhatsApp(phoneNumber: String) {
        val url = "https://api.whatsapp.com/send?phone="
        val message =
            "Hey, i wanna invite you to join the best car selling app ! download from PlayStore - LINK"

        if (appInstalledOrNot("com.whatsapp")) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("$url${phoneNumber}&text=$message")
            startActivity(intent)
        } else {
            Toast.makeText(this, "Whats app not installed on your device", Toast.LENGTH_SHORT)
                .show();
        }
    }

}





