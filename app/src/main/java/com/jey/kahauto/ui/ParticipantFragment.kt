package com.jey.kahauto.ui

import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.jey.kahauto.R
import kotlinx.android.synthetic.main.participant_fragment.*

class ParticipantFragment: Fragment(R.layout.participant_fragment) {

    override fun onResume() {
        super.onResume()

        val participantFirstNameFragment = requireArguments().getString("participant_first_name")
        val participantLastNameFragment = requireArguments().getString("participant_last_name")
        val participantEmailFragment = requireArguments().getString("participant_email")
        val participantPhoneNumberFragment = requireArguments().getString("participant_phone_number")

        val participantsImgPath = requireArguments().getString("participant_img_path")




        participant_first_name.text = "FirstName  -  $participantFirstNameFragment"
        participant_last_name.text = "LastName  -  $participantLastNameFragment"
        participant_email.text = "Email  -  $participantEmailFragment"
        participant_phone_number.text = "PhoneNumber  -  $participantPhoneNumberFragment"

        context?.let { Glide.with(it).load(participantsImgPath).into(participant_profile_img) }
    }

}