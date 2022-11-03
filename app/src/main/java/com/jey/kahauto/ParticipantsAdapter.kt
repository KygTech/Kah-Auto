package com.jey.kahauto

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jey.kahauto.model.Car
import com.jey.kahauto.model.IMAGE_TYPE
import com.jey.kahauto.model.Participants
import com.jey.kahauto.model.User

import kotlinx.android.synthetic.main.participant_card.view.*

class ParticipantsAdapter(
    val context: Context,
    private val participants: MutableList<User>,
    val onParticipantClick: (user: User) -> Unit,
) : RecyclerView.Adapter<ParticipantsAdapter.ParticipantsViewHolder>() {

    class ParticipantsViewHolder(participantView: View) : RecyclerView.ViewHolder(participantView) {

        val participantName: TextView
        val participantCard: CardView

        init {
            participantName = participantView.participant_card_name
            participantCard = participantView.participant_card_item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantsViewHolder {
        return ParticipantsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.participant_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ParticipantsViewHolder, position: Int) {
        val currentParticipant = participants[position]
        holder.participantName.text = currentParticipant.firstName.first().toString() + currentParticipant.lastName.first().toString()


        holder.participantCard.setOnClickListener {
            onParticipantClick(currentParticipant)
        }

    }


    override fun getItemCount(): Int {
        return participants.size
    }

    fun participantsListViewUpdate(participantsList: List<User>) {
        participants.clear()
        participants.addAll(participantsList)
        notifyDataSetChanged()
    }
}