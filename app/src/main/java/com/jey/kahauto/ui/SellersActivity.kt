package com.jey.kahauto.ui

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.jey.kahauto.R
import com.jey.kahauto.SellersListAdapter
import com.jey.kahauto.model.*
import com.jey.kahauto.viewmodel.CarsViewModel
import com.jey.kahauto.viewmodel.SellersListViewModel
import kotlinx.android.synthetic.main.activity_sellers.*
import kotlinx.coroutines.launch


open class BaseActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private fun signOutFromApp() {
        GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut()
        SharedPManager.getInstance(this).sharedPrefs.edit().remove("LAST_LOGIN").apply()
        firebaseAuth.signOut()
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
        finish()
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}

class SellersActivity : BaseActivity() {


    private val sellersListViewModel: SellersListViewModel by viewModels()
    private val carsViewModel: CarsViewModel by viewModels()
    private lateinit var repository: Repository
    private val myUser = SharedPManager.getInstance(this).getMyUser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sellers)

        sellersListViewModel.getAllSellersListsAsLiveData().observe(this) {
            grid_view.adapter = SellersListAdapter(this, it, onSellersListClick())
        }
        repository = Repository.getInstance(this)

        hey_seller.text = "Hello, ${myUser.userName}"

        checkIfSellerHaveList()
    }


    private fun checkIfSellerHaveList() {
        sellersListViewModel.getAllSellersListsAsLiveData().observe(this) { sellerList ->
            sellersListViewModel.viewModelScope.launch {
                sellerList.forEach { sellersList ->
                    sellersList.participants.usersList.forEach { user ->
                        if (user.email == myUser.email) {
                            add_list_button.isVisible = false
                        }
                    }
                }
            }
        }
    }

    private fun onSellersListClick(): (sellersList: SellersList) -> Unit = {
        val intent = Intent(this, CarsActivity::class.java)
        intent.putExtra("listTitle", it.listTitle)
        startActivity(intent)
    }

    fun onAddSellersListClick(view: View) {
        createSellerListAlertDialog()
    }

    private fun createSellerListAlertDialog() {
        val sellerListEditText = EditText(this)

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("New List")
        alertDialogBuilder.setMessage("Give your list a title")
        alertDialogBuilder.setView(sellerListEditText)


        alertDialogBuilder.setPositiveButton("Save") { dialogInterface: DialogInterface, i: Int ->
            val sellerListTitle = sellerListEditText.text.toString()
            val sellerList = SellersList(sellerListTitle, Participants(arrayListOf(myUser)))
            sellersListViewModel.createSellerList(sellerList)
            carsViewModel.setCurrentSellerList(sellerList)

        }

        alertDialogBuilder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int ->
            finish()
        }
        alertDialogBuilder.show()
    }


}