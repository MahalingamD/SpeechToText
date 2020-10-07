package com.maha.voicetranslate

import android.app.Application
import com.google.firebase.FirebaseApp

class AppController :Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}