package com.example.guilt.repository

import androidx.room.*

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend  fun insertApp(apps: Apps)

    @Query("SELECT EXISTS(SELECT * FROM app_table WHERE appName = :appName)")
    suspend fun isAppIsExist(appName: String?) : Boolean

    @Update
    suspend  fun updateApp(app : Apps)

    @Query("SELECT * FROM app_table order by timeStamp desc" )
    suspend fun loadAllApp() : List<Apps>

    @Query("SELECT count(*) FROM app_table")
    suspend fun getCount() : Int

    @Query("SELECT timeStamp FROM APP_TABLE WHERE appName = :appName")
    suspend fun getTimeStamp(appName: String?) : Long
}