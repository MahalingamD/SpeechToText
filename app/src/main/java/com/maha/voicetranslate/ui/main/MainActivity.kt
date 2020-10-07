package com.maha.voicetranslate.ui.main


import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.maha.voicetranslate.R
import com.maha.voicetranslate.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity(),
    MainListener {

    companion object {
        private var myBackPressed: Long = 0
    }

    private val REQ_CODE_SPEECH_INPUT = 100

    private lateinit var mLoginViewModel: MainActivityViewModel

    lateinit var mProgressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mProgressDialog = ProgressDialog(this)

        mLoginViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)


        if(isInternetAvailable(this)){
            started(getString(R.string.pls_wait))
            mLoginViewModel.downloadViewModel()
        }else{
            root_layout.snackbar(getString(R.string.check_internet))
        }

        mLoginViewModel.mMainListener = this

        clickLstener()

    }

    private fun clickLstener() {

        btnSpeak.setOnClickListener {
            eng_heading_txt.hide()
            tur_heading_txt.hide()
            txtSpeechInput_eng.text=""
            txtSpeechInput_tur.text=""
          /*  if(isInternetAvailable(this)) {
                promptSpeechInput()
            }else{
                root_layout.snackbar(getString(R.string.check_internet))
            }*/
              val aText="This is sample text and converted to simple text."
              mLoginViewModel.callTranslate(aText)
              txtSpeechInput_eng.text = aText
        }
    }


    /**
     * Showing google speech input dialog
     */
    private fun promptSpeechInput() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt))
            try {
                startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
            } catch (a: ActivityNotFoundException) {

                root_layout.snackbar(getString(R.string.speech_not_supported))

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            when (requestCode) {
                REQ_CODE_SPEECH_INPUT -> {
                    if (resultCode == RESULT_OK && data != null) {

                        val aArrayList =
                            data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                        if (!aArrayList.isNullOrEmpty()) {

                            txtSpeechInput_eng.text = aArrayList[0]
                            mLoginViewModel.callTranslate(aArrayList[0])

                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun started(aMsg: String) {
        mProgressDialog.setMessage(aMsg)
        mProgressDialog.setCancelable(false)
        mProgressDialog.show()
    }

    override fun success(aMsg: String, aTransTXT: String) {
        mProgressDialog.dismiss()

        if (aMsg == "aTranslate") {
            eng_heading_txt.show()
            tur_heading_txt.show()
            txtSpeechInput_tur.text = aTransTXT
        }
    }

    override fun error(aMsg: String) {
        mProgressDialog.dismiss()
    }

    override fun onBackPressed() {
        exitApp()
    }


    private fun exitApp() {
        try {
            if (myBackPressed + 2000 > System.currentTimeMillis()) {
                val aIntent = Intent(Intent.ACTION_MAIN)
                aIntent.addCategory(Intent.CATEGORY_HOME)
                aIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(aIntent)
                finish()
            } else {
                Toast.makeText(baseContext, getString(R.string.label_exit_alert),
                    Toast.LENGTH_SHORT).show()
                myBackPressed = System.currentTimeMillis()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
