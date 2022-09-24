package com.jey.kahauto


import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.jey.kahauto.model.ApiResponse
import com.jey.kahauto.model.Car
import com.jey.kahauto.model.IMAGE_TYPE
import com.jey.kahauto.model.Repository
import kotlinx.android.synthetic.main.dialog_choose_img.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

object ImagesManager {


    private fun getImageFromGallery(car: Car, getContent: ActivityResultLauncher<Intent>) {
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


//    fun getImageFromCamera(car: Car, getContent: ActivityResultLauncher<Intent>) {
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        getContent.launch(intent)
//    }


//    fun onImageResultFromCamera(result: ActivityResult, chosenCar: Car, context: Context) {
//        if (result.resultCode == AppCompatActivity.RESULT_OK) {
//            val takenImage = result.data?.data
//
//        }
//    }

    fun addImageToCar(car: Car, imagePath: String, imageType: IMAGE_TYPE, context: Context) {
        thread(start = true) {
            Repository.getInstance(context).updateCarImg(car, imagePath, imageType)
        }
    }

    private fun getImageFromApi(car: Car, context: Context) {
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

    fun displayCustomImgDialog(
        context: Context,
        car: Car,
        getContentFromGallery: ActivityResultLauncher<Intent>,
//        getContentFromCamera: ActivityResultLauncher<Intent>,

    ) {

        val mDialogView = LayoutInflater
            .from(context)
            .inflate(R.layout.dialog_choose_img, null)

        val dialog = AlertDialog.Builder(context)
            .setView(mDialogView)
            .create()

        dialog.show()

        val btnGallery = mDialogView.dialog_img_from_gallery
        btnGallery.setOnClickListener {
            getImageFromGallery(car, getContentFromGallery)
            dialog.cancel()

        }

        val btnNetwork = mDialogView.dialog_img_from_network
        btnNetwork.setOnClickListener {
            getImageFromApi(car, context)
            dialog.cancel()
        }

        val btnCamera = mDialogView.dialog_img_from_camera
        btnCamera.setOnClickListener {
//            getImageFromCamera(car, getContentFromCamera)
            dialog.cancel()
        }


        if (car.imagePath == null || car.imagePath == R.drawable.camera_icon_two.toString()) {
            mDialogView.dialog_remove_img.isVisible = false
            mDialogView.dialog_remove_title.isVisible = false
        }
        val btnRemoveImg = mDialogView.dialog_remove_img
        btnRemoveImg.setOnClickListener {
            thread(start = true) {
                Repository.getInstance(context)
                    .updateCarImg(car, R.drawable.camera_icon_two.toString(), IMAGE_TYPE.URI)
            }
            dialog.cancel()
        }

        val btnCancel = mDialogView.dialog_cancel
        btnCancel.setOnClickListener { dialog.cancel() }

    }

}