package com.jey.kahauto


import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

class CarFragment : Fragment(R.layout.car_fragment) {


    override fun onResume() {
        super.onResume()


        val activity = requireActivity()
        val carCompanyView = activity.findViewById<TextView>(R.id.carCompany)
        val carModelView = activity.findViewById<TextView>(R.id.carModel)
        val carYearView = activity.findViewById<TextView>(R.id.carYear)
        val carOwnersView = activity.findViewById<TextView>(R.id.carOwners)
        val carKmView = activity.findViewById<TextView>(R.id.carKm)

        val carImageView = activity.findViewById<ImageView>(R.id.carFragmentImg)

        val carCompany = requireArguments().getString("carCompany")
        val carModel = requireArguments().getString("carModel")
        val carYear = requireArguments().getString("carYear")
        val carOwners = requireArguments().getString("carOwners")
        val carKm = requireArguments().getString("carKm")

        val carImgPath = requireArguments().getString("carImgPath")
        var carImgType = requireArguments().getString("carImgType")


        carCompanyView.text = "Company  -  $carCompany"
        carModelView.text = "Model  -  $carModel"
        carYearView.text = "Year  -  $carYear"
        carOwnersView.text = "Owners  -  $carOwners"
        carKmView.text = "Km  -  $carKm"

        context?.let { Glide.with(it).load(carImgPath).into(carImageView) }
    }

}

