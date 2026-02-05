package com.example.myvideoapp.data

import android.text.TextUtils

object VideoApiConfig {
    // Replace with real App ID
    const val APP_ID = ""

    // Replace with real Session ID
    const val SESSION_ID = ""

    // Replace with real Token
    const val TOKEN = ""

    val isValid: Boolean
        get() = !(TextUtils.isEmpty(APP_ID) ||
                TextUtils.isEmpty(SESSION_ID) ||
                TextUtils.isEmpty(TOKEN))
}