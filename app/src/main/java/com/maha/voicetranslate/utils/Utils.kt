package com.maha.voicetranslate.utils

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.maha.voicetranslate.R


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

fun showAlert(aContext: Context, aMessage: String) {
    try {
        val builder = AlertDialog.Builder(aContext)
        builder.setMessage(aMessage).setTitle(aContext.getString(R.string.app_name))
            .setCancelable(false).setPositiveButton("OK") { dialog, id ->
                dialog.dismiss()
            }


        val alert = builder.create()
        alert.show()
        // Change the buttons color in dialog
        val pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
        pbutton.setTextColor(ContextCompat.getColor(aContext, R.color.black))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun calenderPermission(aContext: Context): Boolean {
    return ContextCompat.checkSelfPermission(aContext,
        Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        aContext,
        Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED
}