package com.maha.voicetranslate.ui.main

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions


class MainActivityViewModel : ViewModel() {

    lateinit var englishTurkishTranslator: FirebaseTranslator

    lateinit var mMainListener: MainListener

    fun setTrnaslateLanguage(aLocale: String): Int {
        return when (aLocale) {
            "ar" -> FirebaseTranslateLanguage.AR
            "en" -> FirebaseTranslateLanguage.EN
            "tr" -> FirebaseTranslateLanguage.TR
            else -> FirebaseTranslateLanguage.TR
        }
    }

    fun downloadViewModel(aSource:String,aTarget:String) {

        try {
            val options = FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(setTrnaslateLanguage(aSource))
                .setTargetLanguage(setTrnaslateLanguage(aTarget))
                .build()
            englishTurkishTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options)


            englishTurkishTranslator.downloadModelIfNeeded()
                .addOnSuccessListener {
                    // Model downloaded successfully. Okay to start translating.
                    mMainListener.success("Success")
                }
                .addOnFailureListener { exception ->
                    // Model couldn’t be downloaded or other internal error.
                    Log.e("error", exception.message ?: "internal error")
                    mMainListener.error("Success")
                }
        } catch (e: Exception) {
            e.printStackTrace()
            mMainListener.error("Success")
        }
    }

    fun callTranslate(aText: String) {

        try {
            englishTurkishTranslator.translate(aText)
                .addOnSuccessListener { translatedText ->
                    // Translation successful.
                    mMainListener.success("aTranslate", translatedText)
                }
                .addOnFailureListener { exception ->
                    // Error.
                    mMainListener.error("aTranslate")
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getLocationFromAddress(aContext:Context,aAddress:String):LatLng?{
        val coder =  Geocoder(aContext)
        val address :List<Address>
        var p1: LatLng? = null

        try {
            address = coder.getFromLocationName(aAddress, 5)
            if (address == null) {
                return null
            }

            val location = address.get(0);
            p1 =  LatLng(location.getLatitude(), location.getLongitude() )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return p1
    }
}


