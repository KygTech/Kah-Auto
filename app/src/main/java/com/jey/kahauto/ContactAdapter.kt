package com.jey.kahauto

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.jey.kahauto.model.Car
import com.jey.kahauto.model.Contact
import com.jey.kahauto.model.User
import kotlinx.android.synthetic.main.contact_row.view.*

class ContactAdapter(
    private var contacts: MutableList<Contact>,
    val onContactClick: (Contact) -> Unit,
    context: Context,


    ) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {


    class ContactViewHolder(contactView: View) : RecyclerView.ViewHolder(contactView) {
        val displayNameTv: TextView
        val phoneNumberTv: TextView
        val contactLayout: ConstraintLayout

        init {
            displayNameTv = contactView.display_name_tv
            phoneNumberTv = contactView.phone_number_tv
            contactLayout = contactView.contact_layout
        }
    }

    fun setFilteredList(filteredList: ArrayList<Contact>) {
        contacts = filteredList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.contact_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val currentContact = contacts[position]
        holder.displayNameTv.text = currentContact.displayName
        holder.phoneNumberTv.text = currentContact.phoneNumber

        holder.contactLayout.setOnClickListener {
            onContactClick(currentContact)
        }

    }

    override fun getItemCount(): Int {
        return contacts.size
    }



}