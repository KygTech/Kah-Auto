package com.jey.kahauto.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.jey.kahauto.R
import com.jey.kahauto.viewmodel.RegistrationViewModel
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment() {

private val registrationViewModel: RegistrationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onStart() {
        super.onStart()
        username_login_et.setText(registrationViewModel.currentUsername)
        username_login_et.addTextChangedListener {
            registrationViewModel.currentUsername = it.toString()
        }
    }

}
