package com.example.guilt.repository

import android.content.Context
import androidx.room.*

@Database(entities = [Apps::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao() : AppDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase? {
           val tempInstance = INSTANCE
            if (tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app.db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}