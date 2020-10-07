package com.maha.voicetranslate.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.maha.voicetranslate.R
import com.maha.voicetranslate.ui.main.MainActivity

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DELAY_TIME: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        callHandler()
    }

    private fun callHandler() {
        try {
            val mHandler = Handler()
            mHandler.postDelayed({
                startMainScreen()
            }, SPLASH_DELAY_TIME)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startMainScreen() {
        val aIntent = Intent(this, MainActivity::class.java)
        startActivity(aIntent)
        this.finish()
    }
}
