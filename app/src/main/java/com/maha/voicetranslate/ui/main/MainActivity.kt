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
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.maha.voicetranslate.R
import com.maha.voicetranslate.model.EventDetail
import com.maha.voicetranslate.ui.map.GoogleMapActivity
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

    var mLanguageList = arrayListOf("Arabic", "English", "Turkish")
    var mFromLang = "en"
    var mToLang = "tr"

    val mEventList = arrayListOf<EventDetail>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mProgressDialog = ProgressDialog(this)

        mLoginViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        modelDownload()
        requestPermission()

        //getCurrentCalaender(this)

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

            if (mFromLang != mToLang) {
                if (isInternetAvailable(this)) {
                    promptSpeechInput()
                } else {
                    root_layout.snackbar(getString(R.string.check_internet))
                }
            } else {
                root_layout.snackbar(getString(R.string.lang_validation))
            }


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
                        0 -> mFromLang = "ar"

                        1 -> mFromLang = "en"

                        2 -> mFromLang = "tr"
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
                        0 -> mToLang = "ar"

                        1 -> mToLang = "en"

                        2 -> mToLang = "tr"
                    }
                    modelDownload()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }

        map_BUT.setOnClickListener {
            if (calenderPermission(this))
                getCurrentCalaender(this)
            else
                requestPermission()
        }
    }

    private fun callMapActivity() {

        if (mEventList.isNullOrEmpty()) {
            showAlert(this, getString(R.string.no_event_today))
        } else {
            val aIntent = Intent(this, GoogleMapActivity::class.java)
            val aBundle = Bundle()
            aBundle.putSerializable("EventList", mEventList)
            aIntent.putExtra("BUNDLE", aBundle)
            startActivity(aIntent)
        }
    }


    fun modelDownload() {
        try {
            if (isInternetAvailable(this@MainActivity)) {
                if (mFromLang != mToLang) {
                    started(getString(R.string.pls_wait))
                    mLoginViewModel.downloadViewModel(mFromLang, mToLang)
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

            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, mFromLang)
            intent.putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayOf(mFromLang))
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
            tur_heading_txt.text = "${getHeadingLocale(mToLang)} Text"
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


    fun getCurrentCalaender(aContext: Context) {

        try {

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

                        val aEventName = aCursor.getString(1)
                        val address = aCursor.getString(5)
                        val aLatlng = mLoginViewModel.getLocationFromAddress(this, address)

                        aLatlng?.let {
                            val aDetail = EventDetail()
                            aDetail.aEventName = aEventName
                            aDetail.aLatitude = it.latitude
                            aDetail.alongitude = it.longitude

                            mEventList.add(aDetail)
                        }

                        // Log.e("aLatlng?.latitude",""+aLatlng?.latitude+"///"+aLatlng?.longitude)
                        aCursor.moveToNext()
                    }
                }
            }

            callMapActivity()

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
                            eng_heading_txt.text = "${getHeadingLocale(mFromLang)} Text"

                            mLoginViewModel.callTranslate(aArrayList[0])

                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

}
