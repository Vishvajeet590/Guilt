package com.example.guilt

import android.Manifest
import android.app.ActivityManager
import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.guilt.databinding.ActivityMainBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!checkUsageStatsPermission()) {
            Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                startActivity(this)
            }
        }

        if (!checkLocationPermission()) {
            requestPermission()
        }

        if (!isMyServiceRunning(GuiltService::class.java)) {
            startService(Intent(this, GuiltService::class.java))
            Log.d("SERVICE", "Service Started....")
        } else
            Log.d("SERVICE", "Service ia already running....")

    }


    private fun isMyServiceRunning(mClass: Class<GuiltService>): Boolean {
        val manager: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        for (service: ActivityManager.RunningServiceInfo in manager.getRunningServices(Int.MAX_VALUE)) {
            if (mClass.name.equals(service.service.className))
                return true
        }

        return false

    }

    private fun checkLocationPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val result1 = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            1
        )
    }

    private fun checkUsageStatsPermission(): Boolean {
        val appOpsManager = getSystemService(AppCompatActivity.APP_OPS_SERVICE) as AppOpsManager
        // `AppOpsManager.checkOpNoThrow` is deprecated from Android Q
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOpsManager.unsafeCheckOpNoThrow(
                "android:get_usage_stats",
                android.os.Process.myUid(), packageName
            )
        } else {
            appOpsManager.checkOpNoThrow(
                "android:get_usage_stats",
                android.os.Process.myUid(), packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }


}