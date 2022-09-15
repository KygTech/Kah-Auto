package com.jey.kahauto

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

object ImagesManager {


    fun getImageFromGallery(car: Car, getContent: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        getContent.launch(intent)
    }

    fun onImageResultFromGallery(result: ActivityResult, chosenCar: Car, context: Context) {
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                addImageToCar(chosenCar, uri.toString(), IMAGE_TYPE.URI, context)
            }
        }
    }

    fun addImageToCar(car: Car, imagePath: String, imageType: IMAGE_TYPE, context: Context) {
        thread(start = true) {
            Repository.getInstance(context).updateCarImg(car, imagePath, imageType)
        }
    }

    fun getImageFromApi(car: Car, context: Context) {
        val retrofit = ApiInterface.create()
        retrofit.getImages(car.company).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                val apiResponse = response.body()
                val apiImage = apiResponse!!.imagesList[2]
                addImageToCar(car, apiImage.imageUrl, IMAGE_TYPE.URL, context)
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.e("Wrong api response", t.message.toString())
            }
        })
    }

    fun displayImageAlertDialog(
        context: Context,
        car: Car,
        getContent: ActivityResultLauncher<Intent>
    ) {
        val alertBuilder = AlertDialog.Builder(context)
        alertBuilder.setTitle("Choose an image")
        alertBuilder.setMessage("Choose image for ${car.company} ${car.model}")

        alertBuilder.setNeutralButton(
            "Cancel"
        ) { dialogInterface: DialogInterface, i: Int -> }

        alertBuilder.setPositiveButton(
            "Gallery"
        ) { dialogInterface: DialogInterface, i: Int ->
         getImageFromGallery(car, getContent)
        }
        alertBuilder.setNegativeButton("Network") { dialogInterface: DialogInterface, i: Int ->
          getImageFromApi(car, context)
        }
        alertBuilder.show()
    }
}