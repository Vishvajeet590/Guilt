package com.example.guilt

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.guilt.repository.AppRepository
import com.example.guilt.repository.Apps
import kotlinx.coroutines.*
import java.util.*
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.*


class GuiltService : Service() {
    var locationStr: String = "d"
    var longitude: String? = "NA"
    var latitude: String? = "NA"
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
/*        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) showNotification() else startForeground(
            123,
            Notification()
        )*/

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {

            Toast.makeText(applicationContext, "Permission required", Toast.LENGTH_LONG).show()
            return
        } else {
            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }

        createNotificationChannel()
        showNotification()
    }


    private val locationRequest: LocationRequest = create().apply {
        interval = 3000
        fastestInterval = 3000
        priority = PRIORITY_BALANCED_POWER_ACCURACY
        maxWaitTime = 5000
    }


    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                val location = locationList.last()
                //    Toast.makeText(this@GuiltService, "Latitude: " + location.latitude.toString() + '\n' +
                //          "Longitude: "+ location.longitude, Toast.LENGTH_LONG).show()
                Log.d("Location-UPDATE", "${location.latitude}  :  ${location.longitude}")
                Log.i("Location i", location.latitude.toString())
                latitude = location.latitude.toString()
                longitude = location.longitude.toString()
            }
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startRepeatingJob(5000L)
        }
        return START_STICKY
    }

    private fun showNotification() {
        val notificationIntet = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this.applicationContext,
            0,
            notificationIntet,
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification
                .Builder(this, "guiltID")
                .setContentText("THIS IS MY SERVICE")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build()
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        startForeground(123, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "guiltID", "Guilt Tracker", NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    @SuppressLint("ServiceCast")
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


    private fun getNonSystemAppsList(): Map<String, String> {
        val appInfos = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val appInfoMap = HashMap<String, String>()
        for (appInfo in appInfos) {
            if (appInfo.flags != ApplicationInfo.FLAG_SYSTEM) {
                appInfoMap[appInfo.packageName] =
                    packageManager.getApplicationLabel(appInfo).toString()
            }
        }
        return appInfoMap
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun startRepeatingJob(timeInterval: Long): Job {
        val rep: AppRepository by lazy {
            AppRepository(this)
        }

        return CoroutineScope(Dispatchers.Default).launch {
            while (NonCancellable.isActive) {
                // add your task here
                if (checkUsageStatsPermission()) {
                    val packages =
                        packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                    val allAppMap = HashMap<String, String>()
                    val cleanAppLocationMap = HashMap<String?, String>()
                    //Getting Package name and mapping to app name
                    for (packageInfo in packages) {
                        allAppMap.put(
                            packageInfo.packageName,
                            packageManager.getApplicationLabel(packageInfo).toString()
                        )
                    }

                    val currentTime = System.currentTimeMillis()
                    val appMap = getNonSystemAppsList();

                    val usageStatsManager: UsageStatsManager =
                        getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

                    val usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,currentTime - (1000 * 60 * 10), currentTime)
                    for (usageStat in usageStats) {
                        //usageEvents.getNextEvent(usageEvent)
                        if (appMap.containsKey(usageStat.packageName)) {
                            if (allAppMap.containsKey(usageStat.packageName) && !cleanAppLocationMap.containsKey(
                                    allAppMap.get(usageStat.packageName)
                                )
                            ) {

                                val appName = allAppMap.get(usageStat.packageName)
                                val packageName = usageStat.packageName


                                Log.e(
                                    "APP-MAP",
                                    "${allAppMap.get(usageStat.packageName)} : ${longitude} -  ${latitude}"
                                )
                                cleanAppLocationMap.put(appName, locationStr)
                                val app = appName?.let {
                                    Apps(
                                        appName = it,
                                        packageName = packageName,
                                        latitude = latitude,
                                        longitude = longitude,
                                        timeStamp = usageStat.lastTimeStamp
                                    )
                                }



                                if(rep.isAppIsExist(appName)){

                                    val appPreviousTimestamp = rep.getTimStamp(appName)
                                    Log.d("UPDATEAPP", "${appName}  :  ${usageStat.lastTimeVisible} - ${appPreviousTimestamp} : ")

                                    //   Log.d("UPDATEDB", "${usageEvent.timeStamp}  :  ${appPreviousTimestamp}")

                                    if( usageStat.lastTimeVisible >0 && usageStat.lastTimeVisible != appPreviousTimestamp){
                                        if (app != null) {
                                            rep.updateApp(app)
                                        }

                                    }

                                }else{
                                    if (app != null) {
                                        rep.insertApp(app)
                                    }
                                    Log.d("UPDATEDB", "${appName} : INSERTING row in DB")
                                }
                            }


                        }
                    }
                    cleanAppLocationMap.clear()


                } else {
                    Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                        startActivity(this)
                    }
                }
                delay(timeInterval)
            }
        }
    }

}