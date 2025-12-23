package com.example.filmler.data.local


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        FilmEntity::class,
        YonetmenEntity::class,
        OyuncuEntity::class,
        TurEntity::class,
        FilmOyuncuCrossRef::class,
        FilmTurCrossRef::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun filmDao(): FilmDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "uygulama_veritabani.db"
                )
                    .createFromAsset("filmler.db")
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}