package com.jey.kahauto

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jey.kahauto.model.Car
import com.jey.kahauto.model.IMAGE_TYPE
import kotlinx.android.synthetic.main.car_item.view.*
import kotlin.concurrent.thread

class CarAdapter(
    private val cars: MutableList<Car>,
    val onCarClick: (Car) -> Unit,
    val onTrashClick: (Car) -> Unit,
    val onAddImgClick: (Car) -> Unit,
    val context: Context
) : RecyclerView.Adapter<CarAdapter.CarsViewHolder>() {

    class CarsViewHolder(carView: View) : RecyclerView.ViewHolder(carView) {
        val tViewCar: TextView
        val btnDelete: Button
        val addImg: ImageView

        init {
            tViewCar = carView.carItem
            btnDelete = carView.btnDeleteCar
            addImg = carView.addCarImg
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

        if (currentCar.imageType != null) {
            if (currentCar.imageType == IMAGE_TYPE.URI) {
                holder.addImg.setImageURI(Uri.parse(currentCar.imagePath))
            } else {
                Glide.with(context).load(currentCar.imagePath).into(holder.addImg)
            }
        }

        holder.tViewCar.setOnClickListener {
            onCarClick(currentCar)
        }

        holder.btnDelete.setOnClickListener {
            thread(start = true) {
                onTrashClick(currentCar)
            }
            notifyItemRemoved(position)
        }

        holder.addImg.setOnClickListener {
            onAddImgClick(currentCar)
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
