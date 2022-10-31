package com.jey.kahauto.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.jey.kahauto.FirebaseManager
import com.jey.kahauto.R
import com.jey.kahauto.model.Repository
import com.jey.kahauto.model.SharedPManager
import com.jey.kahauto.model.User
import com.jey.kahauto.viewmodel.RegistrationViewModel
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RegistrationActivity : AppCompatActivity() {

    private var isLoginFragment = true
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val registrationViewModel: RegistrationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        displayLoginFragment()
        setTextViewClickListener()

    }

    override fun onStart() {
        super.onStart()
        calculateLastLogin()
    }

    private fun calculateLastLogin() {
        val lastLogin = SharedPManager.getInstance(this).getLastLogin()
        if (lastLogin != -1L && System.currentTimeMillis() - lastLogin < 3600000) {
            openSellersActivity()
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
        login_btn.isVisible = false
        btnSignUp.isVisible = true
        isLoginFragment = false
        login_signup_tv.text = "Already a member?? click here to login"
        val signUpFragment = SignUpFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.registration_fragment_view, signUpFragment).commit()


    }

    private fun displayLoginFragment() {
        login_btn.isVisible = true
        btnSignUp.isVisible = false
        isLoginFragment = true
        login_signup_tv.text = "Not a member yet? click here to SignUp"
        val loginFragment = LoginFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.registration_fragment_view, loginFragment).commit()
    }

    fun onSignUpBtnClick(view: View) {

        val firstName = first_name_et.text.toString()
        val lastName = last_name_et.text.toString()
        val password = password_sign_up_et.text.toString()
        val email = email_sign_up_et.text.toString()

        if (firstName.length < 2) {
            makeToast("First name ,  min 2 digits.")
        } else if (lastName.length < 2) {
            makeToast("Last name, min 6 digits")
        } else if (password.length < 6) {
            makeToast("Password must contain min 6 digits")
        } else if (email.isEmpty() || !email.contains("@")) {
            makeToast("Please write valid email")
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    registrationViewModel.viewModelScope.launch(Dispatchers.IO) {
                        val user = User(email, firstName, lastName)
                        FirebaseManager.getInstance(applicationContext).addUser(user)
                            .addOnSuccessListener {
                                displayLoginFragment()
                                makeToast("Success, you can login now")
                            }
                            .addOnFailureListener { "Something went wrong - Exception: ${it.message}" }
                    }
                }
                .addOnFailureListener {
                    makeToast("Failed --  ${it.message}")
                }
        }


    }


    fun onLoginBtnClick(view: View) {
        val email = email_login_et.text.toString()
        val password = password_login_et.text.toString()
        if (email.isEmpty() || !email.contains("@")) {
            makeToast("Email is invalid")
        } else if (password.isEmpty() || password.length < 6) {
            makeToast("Password is too short")
        } else {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    registrationViewModel.viewModelScope.launch(Dispatchers.IO) {
                        FirebaseManager.getInstance(applicationContext).getUser(email)
                            .addOnSuccessListener {
                                val myUser = User(
                                    email,
                                    it.get("firstname").toString(),
                                    it.get("lastname").toString()
                                )
                                goInApp(myUser)
                            }
                            .addOnFailureListener {
                                makeToast("Something went wrong - Exception ${it.message}")
                            }

                    }
                }
                .addOnFailureListener {
                    makeToast("Email or Password are wrong -- ${it.message}")
                }
        }
    }

    fun goInApp(user: User) {
        SharedPManager.getInstance(this).setMyUser(user)
        SharedPManager.getInstance(this).setLastLogin()
        openSellersActivity()
    }


    private fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun openSellersActivity() {
        val intent = Intent(this, SellersActivity::class.java)
        startActivity(intent)
    }
}
