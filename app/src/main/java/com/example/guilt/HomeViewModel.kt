package com.example.guilt

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.guilt.repository.AppDatabase
import com.example.guilt.repository.AppRepository
import com.example.guilt.repository.Apps

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext
    val rep: AppRepository by lazy {
        AppRepository(context)
    }

    suspend fun isDbEmpty(): Boolean {
        if (rep.getCount() > 0) {
            return false
        }
        return true
    }

    suspend fun loadAllData(): List<Apps> {
        val appList = rep.loadAllApp()
        return appList
    }


}