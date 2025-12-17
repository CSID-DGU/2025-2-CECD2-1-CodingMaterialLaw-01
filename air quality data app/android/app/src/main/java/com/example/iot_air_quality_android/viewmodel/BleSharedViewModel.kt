package com.example.iot_air_quality_android.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.iot_air_quality_android.data.model.request.SensorDataRequest

class BleSharedViewModel : ViewModel() {
    private val _connected = MutableStateFlow(false)
    val connected = _connected.asStateFlow()

    private val _deviceName = MutableStateFlow<String?>(null)
    val deviceName = _deviceName.asStateFlow()

    private val _deviceMac = MutableStateFlow<String?>(null)
    val deviceMac = _deviceMac.asStateFlow()

    private val _sensorData = MutableStateFlow<SensorDataRequest?>(null)
    val sensorData = _sensorData.asStateFlow()

    fun setConnected(name: String?, mac: String?, state: Boolean) {
        _deviceName.value = name
        _deviceMac.value = mac
        _connected.value = state
    }

    fun updateSensorData(data: SensorDataRequest) {
        _sensorData.value = data
    }
}
