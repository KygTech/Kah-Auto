package com.jey.kahauto.ui

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.viewModelScope
import com.jey.kahauto.R
import com.jey.kahauto.SellersListAdapter
import com.jey.kahauto.model.*
import com.jey.kahauto.viewmodel.CarsViewModel
import com.jey.kahauto.viewmodel.SellersListViewModel
import kotlinx.android.synthetic.main.activity_sellers.*
import kotlinx.coroutines.launch

class SellersActivity : AppCompatActivity() {

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
                sellerList.forEach {
                    if (it.user.email == myUser.email) {
                        add_list_button.isVisible = false
                    }
                }
            }
        }
    }

    private fun onSellersListClick(): (sellersList: SellersList) -> Unit = {
        val intent = Intent(this, CarsActivity::class.java)
        intent.putExtra("owner", it.owner)
        startActivity(intent)
    }

    fun onAddSellersListClick(view: View) {
        displayOwnerAlertDialog()
    }

    private fun displayOwnerAlertDialog() {
        val sellerListEditText = EditText(this)

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("New List")
        alertDialogBuilder.setMessage("Write your list title")
        alertDialogBuilder.setView(sellerListEditText)


        alertDialogBuilder.setPositiveButton("Save") { dialogInterface: DialogInterface, i: Int ->
            val sellerListTitle = sellerListEditText.text.toString()
            val sellerList = SellersList(sellerListTitle, myUser)
            sellersListViewModel.createSellerList(sellerList)
            carsViewModel.setCurrentSellerList(sellerList)

        }

        alertDialogBuilder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int ->
            finish()
        }
        alertDialogBuilder.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}