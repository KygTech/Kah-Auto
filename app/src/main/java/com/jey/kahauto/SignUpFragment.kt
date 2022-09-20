package com.jey.kahauto

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import kotlin.concurrent.thread

class SignUpFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sing_up, container, false)
    }

    override fun onStart() {
        super.onStart()
        val activity = requireActivity()
        val signUpBtn = activity.findViewById<Button>(R.id.sign_up_btn)
        signUpBtn.setOnClickListener {
            addUser()
        }
    }


    private fun addUser() {
        val activity = requireActivity()
        val username = activity.findViewById<EditText>(R.id.username_sign_up_et).text
        val password = activity.findViewById<EditText>(R.id.password_sign_up_et).text
        val phoneNumber = activity.findViewById<EditText>(R.id.phone_sign_up_et).text
        val email = activity.findViewById<EditText>(R.id.email_sign_up_et).text


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
                requireActivity().findViewById<Button>(R.id.login_button).isVisible=true
            Toast.makeText(context, "Now you can login! enjoy", Toast.LENGTH_LONG).show()
        }
    }
}


