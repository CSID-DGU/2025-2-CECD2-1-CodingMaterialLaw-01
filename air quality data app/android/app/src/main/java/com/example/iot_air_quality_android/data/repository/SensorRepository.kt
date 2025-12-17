package com.example.iot_air_quality_android.data.repository

import android.content.Context
import android.util.Log
import com.example.iot_air_quality_android.data.api.RetrofitInstance
import com.example.iot_air_quality_android.data.model.request.SyncRequest
import com.example.iot_air_quality_android.data.local.AppDatabase
import com.example.iot_air_quality_android.data.local.SensorDataEntity
import com.example.iot_air_quality_android.data.model.request.SensorDataRequest
import com.example.iot_air_quality_android.util.NetworkUtil
import com.example.iot_air_quality_android.util.SyncScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SensorRepository(private val context: Context) {

    private val appContext = context.applicationContext
    private val dao = AppDatabase.getInstance(appContext).sensorCacheDao()
    private val api = RetrofitInstance.api

    suspend fun sendOrCache(data: SensorDataRequest) = withContext(Dispatchers.IO) {
        try {
            if (NetworkUtil.isConnected(appContext)) {
                api.sendAirQualityRealtime(data)
                Log.d("SensorRepo", "✅ Realtime data sent")
            } else {
                dao.insert(data.toEntity())
                Log.w("SensorRepo", "💾 Network down — data cached locally")
                SyncScheduler.scheduleImmediateSync(appContext)
            }
        } catch (e: Exception) {
            Log.e("SensorRepo", "⚠️ Failed to send data, caching instead", e)
            dao.insert(data.toEntity())
            SyncScheduler.scheduleImmediateSync(appContext)
        }
    }

    suspend fun syncCached() = withContext(Dispatchers.IO) {
        val cached = dao.getAll()
        if (cached.isEmpty()) {
            Log.d("SensorRepo", "📂 No cached data to sync")
            return@withContext
        }

        try {
            val request = SyncRequest(cached.map { it.toRequest() })
            val response = api.syncAirQualityData(request)

            if (response.success) {
                dao.deleteByIds(cached.map { it.id })
                Log.d("SensorRepo", "✅ Synced ${cached.size} cached entries")
            } else {
                Log.e("SensorRepo", "⚠️ Sync failed: ${response.error}")
            }
        } catch (e: Exception) {
            Log.e("SensorRepo", "❌ Exception while syncing cache", e)
        }
    }

    private fun SensorDataRequest.toEntity() = SensorDataEntity(
        id = 0, createdAt, pm25Value, pm25Level, pm10Value, pm10Level,
        temperature, temperatureLevel, humidity, humidityLevel,
        co2Value, co2Level, vocValue, vocLevel,
        picoDeviceLatitude, picoDeviceLongitude
    )
}
