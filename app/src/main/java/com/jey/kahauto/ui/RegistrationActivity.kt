package com.jey.kahauto.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.jey.kahauto.FirebaseManager
import com.jey.kahauto.R
import com.jey.kahauto.model.SharedPManager
import com.jey.kahauto.model.User
import com.jey.kahauto.viewmodel.RegistrationViewModel
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.activity_registration.view.*
import kotlinx.android.synthetic.main.dialog_add_car.view.*
import kotlinx.android.synthetic.main.dialog_choose_img.*
import kotlinx.android.synthetic.main.dialog_sign_in.view.*
import kotlinx.android.synthetic.main.dialog_sign_in.view.sign_up_title
import kotlinx.android.synthetic.main.dialog_sign_up.*
import kotlinx.android.synthetic.main.dialog_sign_up.view.*

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistrationActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleGetContent: ActivityResultLauncher<Intent>
    private val registrationViewModel: RegistrationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        googleGetContent = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { content -> onGoogleIntentResult(content) }

    }

    override fun onStart() {
        super.onStart()
        onClickGoogleSignInBtn()
        calculateLastLogin()
    }


    private fun onClickGoogleSignInBtn() {
        val googleSignInOptions = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()
        val googleIntent =
            GoogleSignIn.getClient(this, googleSignInOptions).signInIntent
        google_sign_in_btn.setOnClickListener {
            googleGetContent.launch(googleIntent)
        }
    }

    private fun onGoogleIntentResult(content: ActivityResult) {
        val task: Task<GoogleSignInAccount> =
            GoogleSignIn.getSignedInAccountFromIntent(content.data)
        task.addOnSuccessListener {
            loginOrSignUpToFirebase(it)

        }
        task.addOnFailureListener {
            displayToast("Choose another way to sign in")
        }
    }

    private fun loginOrSignUpToFirebase(googleSignInAccount: GoogleSignInAccount) {
        firebaseAuth.fetchSignInMethodsForEmail(googleSignInAccount.email!!)
            .addOnSuccessListener {
                if (it.signInMethods.isNullOrEmpty()) {
                    registerToKahAutoFirebase(googleSignInAccount)

                } else {
                    val myUser = User(
                        googleSignInAccount.email!!,
                        googleSignInAccount.givenName!!,
                        googleSignInAccount.familyName!!
                    )
                    goInApp(myUser)
                }
            }
            .addOnFailureListener { displayToast("Failed on firebase auth") }
    }

    private fun registerToKahAutoFirebase(googleSignInAccount: GoogleSignInAccount) {
        val authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
        firebaseAuth.signInWithCredential(authCredential)
            .addOnSuccessListener {
                registrationViewModel.viewModelScope.launch(Dispatchers.IO) {
                    val email = googleSignInAccount.email.toString()
                    val firstName = googleSignInAccount.givenName.toString()
                    val lastName = googleSignInAccount.familyName.toString()
                    val user = User(email, firstName, lastName)
                    FirebaseManager.getInstance(this@RegistrationActivity).addUser(user)
                        .addOnSuccessListener {
                            goInApp(user)
                        }
                        .addOnFailureListener { displayToast("Something went wrong - Exception: ${it.message}") }
                }
            }
            .addOnFailureListener { displayToast("Please try again later - Exception: ${it.message}") }
    }


    private fun displaySignInDialog() {

        val myDialogView = layoutInflater
            .inflate(R.layout.dialog_sign_in, null, false)

        val dialog = AlertDialog.Builder(this)
            .setView(myDialogView)
            .create()

        val btnLogin = myDialogView.login_btn
        btnLogin.setOnClickListener {
            onLoginBtnClick(myDialogView)
        }

        val closeSignIn = myDialogView.close_sign_in
        closeSignIn.setOnClickListener {
            dialog.cancel()
        }

        val onSignUpTextClick = myDialogView.sign_up_title
        onSignUpTextClick.setOnClickListener {
            displaySignUpDialog()
            dialog.cancel()

        }

        dialog.show()
    }

    private fun displaySignUpDialog() {
        val myDialogView = layoutInflater
            .inflate(R.layout.dialog_sign_up, null, false)

        val dialog = AlertDialog.Builder(this)
            .setView(myDialogView)
            .create()

        val signUpBtn = myDialogView.sign_up_btn
        signUpBtn.setOnClickListener {
            onSignUpBtnClick(myDialogView)

        }

        val closeDialog = myDialogView.close_sign_up
        closeDialog.setOnClickListener {
            dialog.cancel()
        }

        dialog.show()
    }


    private fun onLoginBtnClick(myDialogView: View) {
        val email = myDialogView.email_sign_in_et.text.toString()
        val password = myDialogView.password_sign_in_et.text.toString()

        if (email.isEmpty() || !email.contains("@")) {
            displayToast("Email is invalid")
        } else if (password.isEmpty() || password.length < 6) {
            displayToast("Password is too short")
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
                                displayToast("Something went wrong - Exception ${it.message}")
                            }

                    }
                }
                .addOnFailureListener {
                    displayToast("Email or Password are wrong -- ${it.message}")
                }
        }
    }

    private fun onSignUpBtnClick(myDialogView: View) {


        val firstName = myDialogView.first_name_et.text.toString()
        val lastName = myDialogView.last_name_et.text.toString()
        val password = myDialogView.password_sign_up_et.text.toString()
        val email = myDialogView.email_sign_up_et.text.toString()

        if (firstName.length < 2) {
            displayToast("First name ,  min 2 digits.")
        } else if (lastName.length < 2) {
            displayToast("Last name, min 2 digits")
        } else if (password.length < 6) {
            displayToast("Password must contain min 6 digits")
        } else if (email.isEmpty() || !email.contains("@")) {
            displayToast("Please write valid email")
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    registrationViewModel.viewModelScope.launch(Dispatchers.IO) {
                        val user = User(email, firstName, lastName)
                        FirebaseManager.getInstance(applicationContext).addUser(user)
                            .addOnSuccessListener {
                          goInApp(user)

                            }
                            .addOnFailureListener { "Something went wrong - Exception: ${it.message}" }
                    }
                }
                .addOnFailureListener {
                    displayToast("Failed --  ${it.message}")
                }
        }


    }


    private fun calculateLastLogin() {
        val lastLogin = SharedPManager.getInstance(this).getLastLogin()
        if (lastLogin != -1L && System.currentTimeMillis() - lastLogin < 3600000) {
            openSellersActivity()
        }
    }

    private fun goInApp(user: User) {
        SharedPManager.getInstance(this).setMyUser(user)
        SharedPManager.getInstance(this).setLastLogin()
        openSellersActivity()
    }


    private fun displayToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun openSellersActivity() {
        val intent = Intent(this, SellersActivity::class.java)
        startActivity(intent)
    }

    fun onSignInBtnClick(view: View) {
        displaySignInDialog()
    }

}
