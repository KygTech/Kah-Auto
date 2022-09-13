package com.jey.kahauto


import android.widget.TextView
import androidx.fragment.app.Fragment

class CarFragment :Fragment(R.layout.car_fragment) {

    override fun onResume() {
        super.onResume()

        val carCompanyView = activity?.findViewById<TextView>(R.id.carCompany)
        val carModelView = activity?.findViewById<TextView>(R.id.carModel)
        val carYearView = activity?.findViewById<TextView>(R.id.carYear)

        val carCompany = requireArguments().getString("carCompany")
        val carModel = requireArguments().getString("carModel")
        val carYear = requireArguments().getString("carYear")


        carCompanyView?.text = "Company  -  $carCompany"
        carModelView?.text = "Model  -  $carModel"
        carYearView?.text= "Year  -  $carYear"

    }

}