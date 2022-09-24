package com.jey.kahauto.ui

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.jey.kahauto.R
import com.jey.kahauto.model.Repository
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.fragment_login.*

class RegistrationActivity : AppCompatActivity() {

    private var isLoginFragment = true
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        displayLoginFragment()
        setTextViewClickListener()

    }

    override fun onStart() {
        super.onStart()
        sharedPreferences = getSharedPreferences(R.string.app_name.toString(), MODE_PRIVATE)
        calculateLastLogin()
    }

    private fun calculateLastLogin() {
        val lastLogin = sharedPreferences.getLong("LAST_LOGIN", -1)
        if (lastLogin != -1L && System.currentTimeMillis() - lastLogin < 3600000) {
            val intent = Intent(this, CarsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setTextViewClickListener() {
        login_signup_tv.setOnClickListener {
            if (isLoginFragment) {
                displaySignUpFragment()
            } else {
                displayLoginFragment()
            }
        }
    }

    private fun displaySignUpFragment() {
        login_button.isVisible = false
        isLoginFragment = false
        login_signup_tv.text = "Already a member?? click here to login"
        val signUpFragment = SignUpFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.registration_fragment_view, signUpFragment).commit()

    }

    private fun displayLoginFragment() {
        login_button.isVisible = true
        isLoginFragment = true
        login_signup_tv.text = "Not a member yet? click here to SignUp"
        val loginFragment = LoginFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.registration_fragment_view, loginFragment).commit()
    }

    fun onStartClick(view: View) {
        if (isUserLegit()) {
            val editor = sharedPreferences.edit()
            editor.putLong("LAST_LOGIN", System.currentTimeMillis()).apply()

            val intent = Intent(this, CarsActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Email or Password are wrong.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun isUserLegit(): Boolean {
        val username = username_login_et.text.toString()
        val password = password_login_et.text.toString()

        return true

//        val usersList = Repository.getInstance(this).getAllUsers()
//        for (user in usersList) {
//            if (username == user.username && password == user.password)
//                return true
//        }
//        return false
    }
}