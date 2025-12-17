package com.example.iot_air_quality_android.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.iot_air_quality_android.data.repository.SensorRepository

/**
 * ✅ 백그라운드 캐시 동기화 워커
 * - 네트워크 연결 시 자동 실행됨
 * - Room 캐시 데이터를 서버로 /sync 전송
 */
class SyncWorker(
    private val ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        return try {
            val repo = SensorRepository(ctx)
            repo.syncCached()
            Log.d("SyncWorker", "✅ Cached data sync complete")
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "❌ Sync failed", e)
            Result.retry()
        }
    }
}
