package com.jey.kahauto.ui


import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.jey.kahauto.R
import kotlinx.android.synthetic.main.car_fragment.*

class CarFragment : Fragment(R.layout.car_fragment) {


    override fun onResume() {
        super.onResume()

        val carCompanyFragment = requireArguments().getString("carCompany")
        val carModelFragment = requireArguments().getString("carModel")
        val carYearFragment = requireArguments().getString("carYear")
        val carOwnersFragment = requireArguments().getString("carOwners")
        val carKmFragment = requireArguments().getString("carKm")

        val carImgPath = requireArguments().getString("carImgPath")



        carCompany.text = "Company  -  $carCompanyFragment"
        carModel.text = "Model  -  $carModelFragment"
        carYear.text = "Year  -  $carYearFragment"
        carOwners.text = "Owners  -  $carOwnersFragment"
        carKm.text = "Km  -  $carKmFragment"

        context?.let { Glide.with(it).load(carImgPath).into(carFragmentImg) }
    }

}

