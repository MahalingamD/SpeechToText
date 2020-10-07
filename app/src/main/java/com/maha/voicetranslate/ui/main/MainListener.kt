package com.maha.voicetranslate.ui.main

interface MainListener {
        fun started(aMsg:String)
        fun success(aMsg:String,aTransTXT:String="")
        fun error(aMsg:String)
}