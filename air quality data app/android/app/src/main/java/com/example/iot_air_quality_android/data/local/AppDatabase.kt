package com.example.iot_air_quality_android.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [SensorDataEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun sensorCacheDao(): SensorCacheDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sensor_cache.db"
                )
                    .fallbackToDestructiveMigration() // 🔹 스키마 변경 시 DB 초기화
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
