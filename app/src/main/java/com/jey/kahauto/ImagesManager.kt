package com.jey.kahauto


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.jey.kahauto.model.ApiResponse
import com.jey.kahauto.model.Car
import com.jey.kahauto.model.IMAGE_TYPE
import com.jey.kahauto.model.Repository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
object ImagesManager {


    fun getImageFromGallery(car: Car, getContent: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        getContent.launch(intent)
    }

    fun captureImageFromCamera(car: Car, getContent: ActivityResultLauncher<Intent>) {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        getContent.launch(cameraIntent)
    }

    fun onImageResultFromCamera(result: ActivityResult, chosenCar: Car, context: Context) {
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as Bitmap
            val bytes = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

            val path = MediaStore.Images.Media.insertImage(
                context.contentResolver,
                imageBitmap,
                "val",
                null
            )
            val uri = Uri.parse(path)
            GlobalScope.launch {
                addImageToCar(chosenCar, uri.toString(), IMAGE_TYPE.URI, context)
            }
        }

    }

    fun onImageResultFromGallery(result: ActivityResult, chosenCar: Car, context: Context) {
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                GlobalScope.launch {
                    addImageToCar(chosenCar, uri.toString(), IMAGE_TYPE.URI, context)
                }
            }
        }
    }

    fun addImageToCar(car: Car, imagePath: String, imageType: IMAGE_TYPE, context: Context) {

        //// May i do GlobalScope on this and delete the others?
            Repository.getInstance(context).updateCarImg(car, imagePath, imageType)
    }

    fun getImageFromApi(car: Car, context: Context) {
        val retrofit = ApiInterface.create()
        retrofit.getImages(car.company).enqueue(object : Callback<ApiResponse> {

            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                val apiResponse = response.body()
                val apiImage = apiResponse!!.imagesList[2]
                GlobalScope.launch {
                    addImageToCar(car, apiImage.imageUrl, IMAGE_TYPE.URL, context)
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.e("Wrong api response", t.message.toString())
            }
        })
    }


}