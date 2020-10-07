package com.maha.voicetranslate.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar


fun ProgressBar.show() {
    visibility = View.VISIBLE
}

fun ProgressBar.hide() {
    visibility = View.GONE
}

fun TextView.show() {
    visibility = View.VISIBLE
}

fun TextView.hide() {
    visibility = View.GONE
}

fun enableImageView(aView:ImageView){
    aView.isEnabled=true
}

fun disableImageView(aView:ImageView){
    aView.isEnabled=true
}

fun View.snackbar(message:String){

    Snackbar.make(this,message,Snackbar.LENGTH_LONG).also { snackbar ->

        snackbar.setAction("Ok"){
            snackbar.dismiss()
        }
    }.show()
}


 fun isInternetAvailable(aContext:Context): Boolean {
    var result = false
     try {
         val connectivityManager =
             aContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
             val networkCapabilities = connectivityManager.activeNetwork ?: return false
             val actNw =
                 connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
             result = when {
                 actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                 actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                 actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                 else -> false
             }
         } else {
             connectivityManager.run {
                 connectivityManager.activeNetworkInfo?.run {
                     result = when (type) {
                         ConnectivityManager.TYPE_WIFI -> true
                         ConnectivityManager.TYPE_MOBILE -> true
                         ConnectivityManager.TYPE_ETHERNET -> true
                         else -> false
                     }

                 }
             }
         }
     } catch (e: Exception) {
         e.printStackTrace()
     }

     return result
}