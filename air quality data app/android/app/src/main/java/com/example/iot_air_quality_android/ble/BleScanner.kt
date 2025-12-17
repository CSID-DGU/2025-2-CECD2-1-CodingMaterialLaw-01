// Deprecated: Scanning now handled by BluetoothDeviceDialogFragment
package com.example.iot_air_quality_android.ble

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.bluetooth.BluetoothDevice
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanCallback
import no.nordicsemi.android.support.v18.scanner.ScanResult
import no.nordicsemi.android.support.v18.scanner.ScanSettings

class BleScanner(
    private val context: Context,
    private val onDeviceFound: (BluetoothDevice) -> Unit
) {

    private val scanner = BluetoothLeScannerCompat.getScanner()
    private val settings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    private val callback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            Log.d("BleScanner", "Device found: ${result.device.address}")
            onDeviceFound(result.device)
        }
    }

    @SuppressLint("MissingPermission")
    fun startScan() {
        scanner.startScan(null, settings, callback)
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        scanner.stopScan(callback)
    }
}
