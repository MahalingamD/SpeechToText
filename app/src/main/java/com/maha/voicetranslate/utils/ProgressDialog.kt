package com.maha.voicetranslate.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.TextView
import com.maha.voicetranslate.R

import com.pnikosis.materialishprogress.ProgressWheel


class ProgressDialog : Dialog {

    private var myContext: Context? = null
    private var myAppContext: Context? = null
    private var myLoadingTxt: TextView? = null
    private var myProgressWheel: ProgressWheel? = null

    constructor(context: Context) : super(context) {
        myContext = context
        try {
            this.requestWindowFeature(Window.FEATURE_NO_TITLE)
            this.setContentView(R.layout.custom_dialog_box)
            this.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            myLoadingTxt = this.findViewById(R.id.custom_dialog_box_TXT_loading) as TextView
            myProgressWheel = this.findViewById(R.id.custom_dialog_box_PB_loading) as ProgressWheel
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * Loading text
     *
     * @param aLoadingText
     */
    fun setMessage(aLoadingText: String) {
        myLoadingTxt!!.text = aLoadingText
    }

}
