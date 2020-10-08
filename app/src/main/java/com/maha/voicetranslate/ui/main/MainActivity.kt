package com.maha.voicetranslate.ui.main



import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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

    var mLanguageList= arrayListOf("Arabic", "English", "Turkish")
    var aFromLang="en"
    var aToLang="tr"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mProgressDialog = ProgressDialog(this)

        mLoginViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        modelDownload()

        setSpinnerAdapter()

        mLoginViewModel.mMainListener = this

        clickListener()

    }

    private fun setSpinnerAdapter() {
        try {
            val aArrayAdapter=ArrayAdapter(this, android.R.layout.simple_spinner_item, mLanguageList)
            aArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            from_locale_spinner.adapter = aArrayAdapter
            from_locale_spinner.setSelection(1)

            to_locale_spinner.adapter = aArrayAdapter
            to_locale_spinner.setSelection(2)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun clickListener() {

        btnSpeak.setOnClickListener {
            eng_heading_txt.hide()
            tur_heading_txt.hide()
            txtSpeechInput_eng.text=""
            txtSpeechInput_tur.text=""

            if(aFromLang!=aToLang) {
                if (isInternetAvailable(this)) {
                    promptSpeechInput()
                } else {
                    root_layout.snackbar(getString(R.string.check_internet))
                }
            }else{
                root_layout.snackbar(getString(R.string.lang_validation))
            }


             /* val aText="This is sample text and converted to simple text."
              mLoginViewModel.callTranslate(aText)
              txtSpeechInput_eng.text = aText
            eng_heading_txt.text="${getHeadingLocale(aFromLang)} Text"*/
        }

        from_locale_spinner.onItemSelectedListener= object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                try {
                    when(position){
                        0-> aFromLang="ar"

                        1-> aFromLang="en"

                        2-> aFromLang="tr"
                    }
                    modelDownload()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        to_locale_spinner.onItemSelectedListener= object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                try {
                    when(position){
                        0-> aToLang="ar"

                        1-> aToLang="en"

                        2-> aToLang="tr"
                    }
                    modelDownload()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
    }


    fun modelDownload(){
        try {
            if(isInternetAvailable(this@MainActivity)){
                if(aFromLang!=aToLang) {
                    started(getString(R.string.pls_wait))
                    mLoginViewModel.downloadViewModel(aFromLang, aToLang)
                }else{
                    root_layout.snackbar(getString(R.string.lang_validation))
                }
            }else{
                root_layout.snackbar(getString(R.string.check_internet))
            }
        } catch (e: Exception) {
            e.printStackTrace()

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

            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, aFromLang)
           // intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, myLocale)
            //intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, myLocale)
            intent.putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES",  arrayOf(aFromLang))
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

    @SuppressLint("SetTextI18n")
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
                            eng_heading_txt.text="${getHeadingLocale(aFromLang)} Text"

                            mLoginViewModel.callTranslate(aArrayList[0])

                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getHeadingLocale(aLocale:String):String{
        return when (aLocale) {
            "ar" -> mLanguageList[0]
            "en" -> mLanguageList[1]
            "tr" -> mLanguageList[2]
            else -> mLanguageList[1]
        }
    }

    override fun started(aMsg: String) {
        mProgressDialog.setMessage(aMsg)
        mProgressDialog.setCancelable(false)
        mProgressDialog.show()
    }

    @SuppressLint("SetTextI18n")
    override fun success(aMsg: String, aTransTXT: String) {
        mProgressDialog.dismiss()

        if (aMsg == "aTranslate") {
            tur_heading_txt.text="${getHeadingLocale(aToLang)} Text"
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
