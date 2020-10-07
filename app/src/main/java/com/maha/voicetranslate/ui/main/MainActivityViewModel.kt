package com.maha.voicetranslate.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions

class MainActivityViewModel :ViewModel() {

    lateinit var  englishGermanTranslator : FirebaseTranslator

    lateinit var mMainListener: MainListener

    fun downloadViewModel(){

        try {
            val options = FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(FirebaseTranslateLanguage.EN)
                .setTargetLanguage(FirebaseTranslateLanguage.TR)
                .build()
            englishGermanTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options)


            englishGermanTranslator.downloadModelIfNeeded()
                .addOnSuccessListener {
                    // Model downloaded successfully. Okay to start translating.
                    mMainListener.success("Success")
                }
                .addOnFailureListener { exception ->
                    // Model couldn’t be downloaded or other internal error.
                    Log.e("error",exception.message?:"internal error")
                    mMainListener.error("Success")
                }
        } catch (e: Exception) {
            e.printStackTrace()
            mMainListener.error("Success")
        }
    }

     fun callTranslate(aText:String) {

         try {
             englishGermanTranslator.translate(aText)
                 .addOnSuccessListener { translatedText ->
                     // Translation successful.
                     mMainListener.success("aTranslate",translatedText)
                 }
                 .addOnFailureListener { exception ->
                     // Error.
                     mMainListener.error("aTranslate")
                 }
         } catch (e: Exception) {
             e.printStackTrace()
         }
     }
}


