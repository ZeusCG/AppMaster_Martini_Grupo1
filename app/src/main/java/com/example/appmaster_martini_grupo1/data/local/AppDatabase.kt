package com.example.appmaster_martini_grupo1.data.local

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.SQLiteConnection
import com.example.appmaster_martini_grupo1.data.model.Favorite
import com.example.appmaster_martini_grupo1.data.model.Profile
import com.example.appmaster_martini_grupo1.data.model.SolicitudContacto
import com.example.appmaster_martini_grupo1.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Database(
    entities = [User::class, Profile::class, Favorite::class, ContentEntity::class, SolicitudContacto::class, ClimaEntity::class],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun profileDao(): ProfileDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun contentDao(): ContentDao
    abstract fun solicitudContactoDao(): SolicitudContactoDao
    abstract fun climaDao(): ClimaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Para sembrar el catálogo sin bloquear la creación de la BD.
        private val seedScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "appmaster_martini.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override suspend fun onCreate(connection: SQLiteConnection) {
                            super.onCreate(connection)
                            // Se ejecuta cuando la BD se crea por primera vez (o tras migración destructiva).
                            seedScope.launch {
                                INSTANCE?.contentDao()?.insertAll(
                                    ContentSeed.items.map { it.toEntity() }
                                )
                            }
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
