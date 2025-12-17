package com.example.iot_air_quality_android.data.ble

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.iot_air_quality_android.ble.BleForegroundService
import com.example.iot_air_quality_android.ble.SensorDataParser
import com.example.iot_air_quality_android.data.model.request.SensorDataRequest
import com.example.iot_air_quality_android.data.repository.SensorRepository
import com.example.iot_air_quality_android.util.LocationUtil
import kotlinx.coroutines.*
import java.util.*

class BleManager(
    private val context: Context,
    private val onSensorData: (SensorDataRequest) -> Unit, // UI 업데이트용 콜백
    private val onDisconnected: (() -> Unit)? = null
) {

    private var bluetoothGatt: BluetoothGatt? = null
    private var latestData: SensorDataRequest? = null
    private var sendJob: Job? = null
    private val repository = SensorRepository(context)

    companion object {
        private val SERVICE_DATA_UUID =
            UUID.fromString("0000FFB0-0000-1000-8000-00805F9B34FB")
        private val SERVICE_CTRL_UUID =
            UUID.fromString("0000FFE0-0000-1000-8000-00805F9B34FB")

        private val SENSOR_DATA_UUID =
            UUID.fromString("0000FFB3-0000-1000-8000-00805F9B34FB")
        private val DUST_SENSOR_UUID =
            UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB")
        private val GAS_SENSOR_UUID =
            UUID.fromString("0000FFD1-0000-1000-8000-00805F9B34FB")
        private val TH_SENSOR_UUID =
            UUID.fromString("0000FFC1-0000-1000-8000-00805F9B34FB")

        private val CLIENT_CHARACTERISTIC_CONFIG_UUID =
            UUID.fromString("00002902-0000-1000-8000-00805F9B34FB")
    }

    @SuppressLint("MissingPermission")
    fun connect(device: BluetoothDevice) {
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
        sendJob?.cancel()
    }

    private val gattCallback = object : BluetoothGattCallback() {

//        @SuppressLint("MissingPermission")
//        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
//            if (newState == BluetoothProfile.STATE_CONNECTED) {
//                Log.d("BleManager", "✅ Connected to GATT server")
//                gatt.discoverServices()
//            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
//                Log.d("BleManager", "❌ Disconnected from GATT server")
//                bluetoothGatt = null
//                sendJob?.cancel()
//                onDisconnected?.invoke()
//
//                try {
//                    BleForegroundService.stop(context)
//                    Log.d("BleManager", "🧹 Service stopped after disconnection")
//                } catch (e: Exception) {
//                    Log.e("BleManager", "⚠️ Failed to stop service cleanly", e)
//                }
//            }
//        }
@SuppressLint("MissingPermission")
override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
    if (newState == BluetoothProfile.STATE_CONNECTED) {
        Log.d("BleManager", "✅ Connected to GATT server")

        // 연결 브로드캐스트 (UI와 상태 동기화)
        val b = Intent("BLE_CONNECTION").apply {
            putExtra("connected", true)
            putExtra("name", gatt.device?.name)
            putExtra("mac", gatt.device?.address)
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(b)

        gatt.discoverServices()
    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
        Log.d("BleManager", "❌ Disconnected from GATT server")
        bluetoothGatt = null
        sendJob?.cancel()

        // 해제 브로드캐스트
        val b = Intent("BLE_CONNECTION").apply {
            putExtra("connected", false)
            putExtra("name", gatt.device?.name)
            putExtra("mac", gatt.device?.address)
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(b)

        onDisconnected?.invoke()

        try {
            BleForegroundService.stop(context)
            Log.d("BleManager", "🧹 Service stopped after disconnection")
        } catch (e: Exception) {
            Log.e("BleManager", "⚠️ Failed to stop service cleanly", e)
        }
    }
}

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.e("BleManager", "❌ Service discovery failed: $status")
                return
            }

            val services = gatt.services
            Log.d("BleManager", "🔍 Discovered ${services.size} services")

            services.forEach { service ->
                Log.d("BleManager", "➡️ Service UUID: ${service.uuid}")
                service.characteristics?.forEach { ch ->
                    Log.d("BleManager", "   📡 Characteristic UUID: ${ch.uuid}")

                    when (ch.uuid.toString().uppercase(Locale.ROOT)) {
                        "0000FFE1-0000-1000-8000-00805F9B34FB" -> {
                            GlobalScope.launch(Dispatchers.IO) {
                                delay(300)
                                enableSensor(gatt, service, ch.uuid, 0x01)
                            }
                        }
                        "0000FFD1-0000-1000-8000-00805F9B34FB" -> {
                            GlobalScope.launch(Dispatchers.IO) {
                                delay(600)
                                enableSensor(gatt, service, ch.uuid, 0x01)
                            }
                        }
                        "0000FFC1-0000-1000-8000-00805F9B34FB" -> {
                            GlobalScope.launch(Dispatchers.IO) {
                                delay(900)
                                enableSensor(gatt, service, ch.uuid, 0x01)
                            }
                        }
                        "0000FFB3-0000-1000-8000-00805F9B34FB" -> {
                            GlobalScope.launch(Dispatchers.IO) {
                                delay(1200)
                                enableNotifications(gatt, ch)
                            }
                        }
                    }
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            if (characteristic.uuid == SENSOR_DATA_UUID) {
                val data = characteristic.value

                LocationUtil.getLastLocation(context) { lat, lng ->
                    val newData = SensorDataParser.parse(data, lat, lng)
                    latestData = newData
                    onSensorData(newData)

                    // 전송 루프 시작 (한 번만)
                    if (sendJob == null || sendJob?.isCancelled == true) {
                        sendJob = CoroutineScope(Dispatchers.IO).launch {
                            while (isActive) {
                                latestData?.let {
                                    try {
                                        repository.sendOrCache(it)
                                        Log.d("BleManager", "📤 Sent latest data: ${it.createdAt}")
                                    } catch (e: Exception) {
                                        Log.e("BleManager", "❌ Failed to send/cache data", e)
                                    }
                                }
                                delay(3000)
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableSensor(
        gatt: BluetoothGatt,
        service: BluetoothGattService,
        uuid: UUID,
        value: Int
    ) {
        val ch = service.getCharacteristic(uuid)
        if (ch != null) {
            ch.value = byteArrayOf(value.toByte())
            val result = gatt.writeCharacteristic(ch)
            Log.d("BleManager", "🧩 enableSensor ${uuid} -> $value, result=$result")
        } else {
            Log.w("BleManager", "⚠️ enableSensor characteristic not found for $uuid")
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableNotifications(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        gatt.setCharacteristicNotification(characteristic, true)
        val descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID)
        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        val result = gatt.writeDescriptor(descriptor)
        Log.d("BleManager", "🔔 writeDescriptor result = $result")
        Log.d("BleManager", "🔔 Notifications enabled for sensor data")
    }
}
