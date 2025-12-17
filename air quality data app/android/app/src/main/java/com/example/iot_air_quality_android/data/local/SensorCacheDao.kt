package com.example.iot_air_quality_android.data.local

import androidx.room.*

@Dao
interface SensorCacheDao {

    /**
     * ✅ 캐시 데이터 저장
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: SensorDataEntity)

    /**
     * ✅ 모든 캐시 데이터 가져오기
     */
    @Query("SELECT * FROM sensor_cache ORDER BY id ASC")
    suspend fun getAll(): List<SensorDataEntity>

    /**
     * ✅ 여러 개의 캐시 데이터 한꺼번에 삭제
     */
    @Query("DELETE FROM sensor_cache WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Int>)

    /**
     * ✅ 전체 캐시 삭제 (테스트/리셋용)
     */
    @Query("DELETE FROM sensor_cache")
    suspend fun clearAll()
}
