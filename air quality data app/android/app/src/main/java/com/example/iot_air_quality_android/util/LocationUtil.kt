package com.example.iot_air_quality_android.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

object LocationUtil {

    const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    const val LOCATION_SERVICE_REQUEST_CODE = 2001

    fun checkAndRequestPermissions(activity: Activity): Boolean {
        Log.d("LocationUtil", "checkAndRequestPermissions() called")

        val fineLocation = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocation = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)

        // 이미 권한이 있으면 true
        if (fineLocation == PackageManager.PERMISSION_GRANTED &&
            coarseLocation == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("LocationUtil", "위치 권한 허용 상태")
            return true
        }

        // 권한이 없으면 요청
        Log.d("LocationUtil", "권한 요청 시작")
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )

        return false
    }

    /** ✅ 위치 서비스(GPS) 활성화 확인 및 팝업 요청 */
    fun ensureLocationServiceEnabled(activity: Activity) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(activity)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            Log.d("LocationUtil", "위치 서비스가 이미 켜져 있음")
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    Log.w("LocationUtil", "위치 서비스 비활성화 상태 → 다이얼로그 표시")
                    exception.startResolutionForResult(activity, LOCATION_SERVICE_REQUEST_CODE)
                } catch (sendEx: Exception) {
                    Log.e("LocationUtil", "startResolutionForResult 실패: ${sendEx.message}")
                }
            } else {
                Log.e("LocationUtil", "위치 설정 확인 실패: ${exception.message}")
            }
        }
    }

    /** ✅ 마지막 위치 가져오기 */
    @SuppressLint("MissingPermission")
    fun getLastLocation(context: Context, callback: (Double, Double) -> Unit) {
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)
        fusedClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    callback(location.latitude, location.longitude)
                } else {
                    callback(0.0, 0.0)
                }
            }
            .addOnFailureListener {
                callback(0.0, 0.0)
            }
    }

    /** ✅ “다시 묻지 않기” 이후 설정 이동 Dialog */
    fun showPermissionDeniedDialog(activity: Activity) {
        AlertDialog.Builder(activity)
            .setTitle("위치 권한 필요")
            .setMessage("앱의 기능을 사용하려면 위치 권한이 필요합니다.\n설정 화면에서 권한을 허용해 주세요.")
            .setPositiveButton("설정으로 이동") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", activity.packageName, null)
                }
                activity.startActivity(intent)
                activity.finishAffinity()
            }
            .setNegativeButton("종료") { _, _ ->
                activity.finishAffinity()
            }
            .setCancelable(false)
            .show()
    }
}