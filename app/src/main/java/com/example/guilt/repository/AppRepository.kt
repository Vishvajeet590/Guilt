package com.example.guilt.repository

import android.content.Context
import android.os.AsyncTask
//import com.example.guilt.AppInfo

class AppRepository(context: Context) {
    var db : AppDao = AppDatabase.getInstance(context)?.appDao()!!

    suspend fun insertApp(apps: Apps ){
       db.insertApp(apps)
    }

    suspend fun isAppIsExist(appName: String?): Boolean {
        return db.isAppIsExist(appName)
    }

    suspend fun updateApp(app : Apps){
        db.updateApp(app)
    }

    suspend fun loadAllApp() : List<Apps>{
        return db.loadAllApp()
    }

    suspend fun getCount() : Int{
        return db.getCount()
    }

/*

    private class insertAsyncTask internal constructor(private val appDao: AppDao): AsyncTask<Apps,Void, Void>(){
        override suspend fun doInBackground(vararg p0: Apps): Void? {
            appDao.insertApp(p0[0])
            return null
            }

    }*/
}