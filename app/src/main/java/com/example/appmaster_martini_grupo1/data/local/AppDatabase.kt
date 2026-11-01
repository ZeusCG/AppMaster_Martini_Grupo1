package com.example.appmaster_martini_grupo1.data.local

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import com.example.appmaster_martini_grupo1.data.model.Profile
import com.example.appmaster_martini_grupo1.data.model.User

@Database(
    entities = [User::class, Profile::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun profileDao(): ProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "appmaster_martini.db"
                ).build().also { INSTANCE = it }
            }
    }
}