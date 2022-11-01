//package com.jey.kahauto.ui
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.core.widget.addTextChangedListener
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.activityViewModels
//import com.jey.kahauto.R
//import com.jey.kahauto.viewmodel.RegistrationViewModel
//import kotlinx.android.synthetic.main.fragment_sign_up.*

//
//class SignUpFragment : Fragment() {
//
//    private val registrationViewModel: RegistrationViewModel by activityViewModels()
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
//        return inflater.inflate(R.layout.dialog_sign_up, container, false)
//    }
//
//    override fun onStart() {
//        super.onStart()
//        email_sign_up_et.setText(registrationViewModel.currentEmail)
//        email_sign_up_et.addTextChangedListener {
//            registrationViewModel.currentEmail = it.toString()
//
//        }
//    }
//
//
//}
//
//
//




