package com.example.guilt.repository

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_table")
data class Apps(
    var appId : Int? = null,
    @NonNull
    @PrimaryKey(autoGenerate = false)
    var appName : String  ="unknown",
    val packageName : String? ="unknown",
    val longitude : String? ="unknown",
    val latitude : String? ="unknown",
    val timeStamp : Long? =0
)