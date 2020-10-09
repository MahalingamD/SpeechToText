package com.maha.voicetranslate.ui.main


import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.maha.voicetranslate.R
import com.maha.voicetranslate.ui.map.GoogleMapActivity
import com.maha.voicetranslate.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(),
    MainListener {

    companion object {
        private var myBackPressed: Long = 0
    }

    private val REQ_CODE_SPEECH_INPUT = 100

    private lateinit var mLoginViewModel: MainActivityViewModel

    lateinit var mProgressDialog: ProgressDialog

    var mLanguageList = arrayListOf("Arabic", "English", "Turkish")
    var aFromLang = "en"
    var aToLang = "tr"

    val aLatlngList= arrayListOf<LatLng>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mProgressDialog = ProgressDialog(this)

        mLoginViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        modelDownload()
        requestPermission()

        getCurrentCalaender(this)

        setSpinnerAdapter()

        mLoginViewModel.mMainListener = this

        clickListener()

    }

    private fun setSpinnerAdapter() {
        try {
            val aArrayAdapter =
                ArrayAdapter(this, android.R.layout.simple_spinner_item, mLanguageList)
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
            txtSpeechInput_eng.text = ""
            txtSpeechInput_tur.text = ""

           /* if (aFromLang != aToLang) {
                if (isInternetAvailable(this)) {
                    promptSpeechInput()
                } else {
                    root_layout.snackbar(getString(R.string.check_internet))
                }
            } else {
                root_layout.snackbar(getString(R.string.lang_validation))
            }*/

            val aIntent=Intent(this,GoogleMapActivity::class.java)

            aIntent.putExtra("latlng",aLatlngList)
            startActivity(aIntent)


            /* val aText="This is sample text and converted to simple text."
             mLoginViewModel.callTranslate(aText)
             txtSpeechInput_eng.text = aText
           eng_heading_txt.text="${getHeadingLocale(aFromLang)} Text"*/
        }

        from_locale_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                try {
                    when (position) {
                        0 -> aFromLang = "ar"

                        1 -> aFromLang = "en"

                        2 -> aFromLang = "tr"
                    }
                    modelDownload()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        to_locale_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                try {
                    when (position) {
                        0 -> aToLang = "ar"

                        1 -> aToLang = "en"

                        2 -> aToLang = "tr"
                    }
                    modelDownload()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
    }


    fun modelDownload() {
        try {
            if (isInternetAvailable(this@MainActivity)) {
                if (aFromLang != aToLang) {
                    started(getString(R.string.pls_wait))
                    mLoginViewModel.downloadViewModel(aFromLang, aToLang)
                } else {
                    root_layout.snackbar(getString(R.string.lang_validation))
                }
            } else {
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
            intent.putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayOf(aFromLang))
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
                            eng_heading_txt.text = "${getHeadingLocale(aFromLang)} Text"

                            mLoginViewModel.callTranslate(aArrayList[0])

                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getHeadingLocale(aLocale: String): String {
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
            tur_heading_txt.text = "${getHeadingLocale(aToLang)} Text"
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
                Toast.makeText(
                    baseContext, getString(R.string.label_exit_alert),
                    Toast.LENGTH_SHORT
                ).show()
                myBackPressed = System.currentTimeMillis()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCurrentCalaender(aContext: Context) {

        try {
            val nameOfEvent = arrayListOf<String>()
            val sDate = arrayListOf<String>()
            val eDate = arrayListOf<String>()
            val eDesc = arrayListOf<String>()


            val startTime = Calendar.getInstance()
            startTime.set(Calendar.HOUR_OF_DAY, 0)
            startTime.set(Calendar.MINUTE, 0)
            startTime.set(Calendar.SECOND, 0)

            val endTime = Calendar.getInstance()
            endTime.set(Calendar.HOUR_OF_DAY, 23)
            endTime.set(Calendar.MINUTE, 59)
            endTime.set(Calendar.SECOND, 59)
            endTime.add(Calendar.DATE, 0)




          //  val selection = "( " + CalendarContract.Events.DTSTART + " >= " + startTime.getTimeInMillis() + " AND " + CalendarContract.Events.DTEND + " <= " + endTime.getTimeInMillis() + " )"

            val selection =
                "( " + CalendarContract.Events.DTSTART + " >= " + startTime.timeInMillis + " AND " + CalendarContract.Events.DTEND + " <= " + endTime.timeInMillis + " )"

            val aCursor = aContext.contentResolver.query(
                Uri.parse("content://com.android.calendar/events"),
                arrayOf("calendar_id", "title", "description", "dtstart", "dtend", "eventLocation"),
                selection,
                null,
                null
            )

            if (aCursor != null) {
                aCursor.moveToFirst()

                if (aCursor.count > 0) {
                    while (!aCursor.isAfterLast) {
                        nameOfEvent.add(aCursor.getString(1))
                        eDesc.add(aCursor.getString(2))
                        sDate.add(aCursor.getString(3))
                        eDate.add(aCursor.getString(4))
                       val address=aCursor.getString(5)

                       val aLatlng= mLoginViewModel.getLocationFromAddress(this,address)

                        aLatlng?.let {
                            aLatlngList.add(it)
                        }


                        Log.e("aLatlng?.latitude",""+aLatlng?.latitude+"///"+aLatlng?.longitude)
                       aCursor.moveToNext()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private fun requestPermission() {
        try {
            Dexter.withActivity(this).withPermissions(
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            //Toast.makeText(mContext, "All permissions are granted!", Toast.LENGTH_SHORT).show()-
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied) {
                            // show alert dialog navigating to Settings
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>, token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).withErrorListener {
                    Toast.makeText(this, "Error occurred! ", Toast.LENGTH_SHORT).show()
                }.onSameThread().check()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
