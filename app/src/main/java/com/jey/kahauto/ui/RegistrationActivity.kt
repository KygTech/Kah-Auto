package com.jey.kahauto.ui

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.jey.kahauto.R
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_sing_up.*


class RegistrationActivity : AppCompatActivity() {

    private var isLoginFragment = true
    private lateinit var sharedPreferences: SharedPreferences
    private val firebaseAuth = FirebaseAuth.getInstance()

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
        btnSignUp.isVisible = true
        isLoginFragment = false
        login_signup_tv.text = "Already a member?? click here to login"
        val signUpFragment = SignUpFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.registration_fragment_view, signUpFragment).commit()

    }

    private fun displayLoginFragment() {
        login_button.isVisible = true
        btnSignUp.isVisible = false
        isLoginFragment = true
        login_signup_tv.text = "Not a member yet? click here to SignUp"
        val loginFragment = LoginFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.registration_fragment_view, loginFragment).commit()
    }

    fun onSignUpBtnClick(view: View) {

        val email = email_sign_up_et.text.toString()
        val password = password_sign_up_et.text.toString()

        if (email.isEmpty()) {
            makeToast("Please write your email")
        } else if (password.isEmpty() || password.length < 6) {
            makeToast("Password must contain min 6 digits")
        } else {
            if (email.contains("@") && password.length >= 6) {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        makeToast("Success, you can login now")
                        displayLoginFragment()
                    }
                    .addOnFailureListener {
                        makeToast("Failed")
                    }
            } else {
                makeToast("Email or password are not as required")
            }

        }
    }


    fun onLoginBtnClick(view: View) {
        val email = username_login_et.text.toString()
        val password = password_login_et.text.toString()
        if (email.isEmpty()) {
            makeToast("Email is missing")
        } else if (password.isEmpty()) {
            makeToast("Password is missing")
        } else {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    goInApp(email)
                }
                .addOnFailureListener {
                    makeToast("Email or Password are wrong")
                }
        }
    }

    fun goInApp(userName: String) {
        val editor = sharedPreferences.edit()
        editor.putLong("LAST_LOGIN", System.currentTimeMillis()).apply()
        editor.putString("USER_NAME", userName).apply()
        val intent = Intent(this, CarsActivity::class.java)
        startActivity(intent)
    }

    private fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

}
