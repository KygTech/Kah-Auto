package com.jey.kahauto.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jey.kahauto.R
import com.jey.kahauto.viewmodel.RegistrationViewModel
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.google_sign_in_btn
import kotlinx.android.synthetic.main.fragment_sing_up.*

class LoginFragment : Fragment() {

private val registrationViewModel: RegistrationViewModel by activityViewModels()
    private lateinit var googleGetContent: ActivityResultLauncher<Intent>
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        googleGetContent = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { content ->
            onGoogleIntentResult(content)
        }
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onStart() {
        super.onStart()
        username_login_et.setText(registrationViewModel.currentUsername)
        username_login_et.addTextChangedListener {
            registrationViewModel.currentUsername = it.toString()
        }
        onClickGoogleSignInBtn()
    }



    private fun onClickGoogleSignInBtn() {
        val googleSignInOptions = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()
        val googleIntent =
            GoogleSignIn.getClient(requireActivity(), googleSignInOptions).signInIntent
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
                    getIntoApp(googleSignInAccount.displayName.toString())
                }
            }
            .addOnFailureListener { displayToast("Failed on firebase auth") }
    }

    private fun registerToKahAutoFirebase(googleSignInAccount: GoogleSignInAccount) {
        val authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
        firebaseAuth.signInWithCredential(authCredential)
            .addOnSuccessListener {
                getIntoApp(googleSignInAccount.displayName.toString())
            }
            .addOnFailureListener { displayToast("Please try again later - Exception: ${it.message}") }
    }

    private fun getIntoApp(userName:String) {
        (requireActivity() as RegistrationActivity).goInApp(userName)
    }

    private fun displayToast(text: String) {
        Toast.makeText(requireActivity(), text, Toast.LENGTH_SHORT).show()
    }


}
