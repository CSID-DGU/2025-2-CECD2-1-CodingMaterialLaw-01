package com.example.iot_air_quality_android.ble

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.iot_air_quality_android.R
import com.example.iot_air_quality_android.data.ble.BleManager
import com.example.iot_air_quality_android.data.model.request.SensorDataRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BleForegroundService : Service() {

    private var bleManager: BleManager? = null
    private var connectedDevice: BluetoothDevice? = null

    override fun onBind(intent: Intent?): IBinder? = null

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("BleService", "🟢 Service started")

        val device = intent?.getParcelableExtra<BluetoothDevice>("device")
        connectedDevice = device

        if (device == null) {
            Log.e("BleService", "❌ No device passed to service")
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground(NOTIF_ID, createNotification(device.name ?: "Unknown device"))

        // 연결/해제 이벤트를 브로드캐스트
        fun broadcastConnection(connected: Boolean) {
            val b = Intent("BLE_CONNECTION").apply {
                putExtra("connected", connected)
                putExtra("name", device.name)
                putExtra("mac", device.address)
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(b)
        }

        // BLE 연결 및 실시간 전송 시작
        bleManager = BleManager(
            context = this,
            onSensorData = { data -> handleSensorData(data) },
            onDisconnected = {
                Log.w("BleService", "⚠️ Device disconnected")
                broadcastConnection(false)
                stopSelf()
            }
        )

        // 연결 시점은 GATT 콜백에서 잡히므로 BleManager 내부에서 broadcast 하게끔 보완되어 있음
        bleManager?.connect(device)

        // (선택) 즉시 “연결 시도중” UI 반영은 Fragment가 처리
        return START_STICKY
    }

    private fun handleSensorData(data: SensorDataRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("BleService", "📡 SensorData received: $data")
            val intent = Intent("BLE_SENSOR_DATA").apply {
                putExtra("sensor_data", data)
            }
            LocalBroadcastManager.getInstance(this@BleForegroundService).sendBroadcast(intent)
        }
    }

    private fun createNotification(deviceName: String): Notification {
        val channelId = "ble_foreground_channel"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "BLE Foreground Service",
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Connected to $deviceName")
            .setContentText("Streaming real-time air quality data")
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        bleManager?.disconnect()
        bleManager = null

        // 서비스 종료 시 연결 해제 브로드캐스트(이중 안전망)
        connectedDevice?.let { dev ->
            val b = Intent("BLE_CONNECTION").apply {
                putExtra("connected", false)
                putExtra("name", dev.name)
                putExtra("mac", dev.address)
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(b)
        }

        Log.d("BleService", "🔴 Service stopped")
    }

    companion object {
        private const val NOTIF_ID = 101

        fun start(context: Context, device: BluetoothDevice) {
            val intent = Intent(context, BleForegroundService::class.java).apply {
                putExtra("device", device)
            }
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, BleForegroundService::class.java)
            context.stopService(intent)
        }

        fun isRunning(context: Context): Boolean {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
            return manager.getRunningServices(Int.MAX_VALUE)
                .any { it.service.className == BleForegroundService::class.java.name }
        }
    }
}
