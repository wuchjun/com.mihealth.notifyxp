package com.mihealth.notifysettings

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    var selected: Boolean = false
)
