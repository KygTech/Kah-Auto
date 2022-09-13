package com.jey.kahauto

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.concurrent.thread

class CarAdapter(
    private val cars: MutableList<Car>,
    val onCarClick : (Car) -> Unit,
    val onTrashClick : (Car) -> Unit
) : RecyclerView.Adapter<CarAdapter.CarsViewHolder>() {

    class CarsViewHolder(carView: View) : RecyclerView.ViewHolder(carView) {
        val tViewCar: TextView
        val btnDelete: Button

        init {
            tViewCar = carView.findViewById(R.id.carItem)
            btnDelete = carView.findViewById(R.id.btnDeleteCar)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarsViewHolder {
        return CarsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.car_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CarsViewHolder, position: Int) {
     val currentCar = cars[position]
        holder.tViewCar.text = "${currentCar.company} - ${currentCar.model}"

        holder.tViewCar.setOnClickListener {
            onCarClick(currentCar)
        }

        holder.btnDelete.setOnClickListener {
            thread (start = true){
                onTrashClick(currentCar)
            }
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount(): Int {
        return cars.size
    }

    fun carsListViewUpdate(carsList: List<Car>) {
        cars.clear()
        cars.addAll(carsList)
        notifyDataSetChanged()
    }

}