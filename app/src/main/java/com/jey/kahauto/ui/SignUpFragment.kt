package com.jey.kahauto.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.jey.kahauto.R
import com.jey.kahauto.viewmodel.RegistrationViewModel
import com.jey.kahauto.model.Repository
import com.jey.kahauto.model.User
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_sing_up.*
import kotlin.concurrent.thread

class SignUpFragment : Fragment() {

    private val registrationViewModel: RegistrationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sing_up, container, false)
    }

    override fun onStart() {
        super.onStart()

        username_login_et.setText(registrationViewModel.currentUsername)
        username_login_et.addTextChangedListener {
            registrationViewModel.currentUsername = it.toString()
        }
        sign_up_btn.setOnClickListener {
            addUser()
        }
    }


    private fun addUser() {
        val activity = requireActivity()
        val username = username_sign_up_et.text
        val password = password_sign_up_et.text
        val phoneNumber = phone_sign_up_et.text
        val email =email_sign_up_et.text


        if (username.isEmpty()) {
            Toast.makeText(context, "Add car company", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            Toast.makeText(context, "Add car model", Toast.LENGTH_SHORT).show()
        } else if (phoneNumber.isEmpty()) {
            Toast.makeText(context, "Add car year", Toast.LENGTH_SHORT).show()
        } else if (email.isEmpty()) {
            Toast.makeText(context, "Add car owners", Toast.LENGTH_SHORT).show()
        } else {
            val user = User(
                username.toString(),
                password.toString(),
                phoneNumber.toString(),
                email.toString()
            )
            thread(start = true) {
                Repository.getInstance(activity).addUser(user)
            }
            username.clear()
            password.clear()
            phoneNumber.clear()
            email.clear()

            val loginFragment = LoginFragment()

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.registration_fragment_view, loginFragment).commit()
            login_button.isVisible = true
            Toast.makeText(context, "Now you can login! enjoy", Toast.LENGTH_LONG).show()
        }
    }
}



