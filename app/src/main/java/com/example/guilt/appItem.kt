package com.example.guilt

import android.graphics.drawable.Drawable

data class AppItem(
    var appName : String  ="unknown",
    val packageName : String? ="unknown",
    val longitude : String? ="unknown",
    val latitude : String? ="unknown",
    val totalUsage : Long? =0,
    val appIcon : Drawable? = null
)