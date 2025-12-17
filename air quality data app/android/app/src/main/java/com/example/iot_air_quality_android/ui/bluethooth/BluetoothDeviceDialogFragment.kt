//package com.example.iot_air_quality_android.ui.bluetooth
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.app.AlertDialog
//import android.app.Dialog
//import android.bluetooth.BluetoothAdapter
//import android.bluetooth.BluetoothManager
//import android.bluetooth.le.BluetoothLeScanner
//import android.bluetooth.le.ScanCallback
//import android.bluetooth.le.ScanFilter
//import android.bluetooth.le.ScanResult
//import android.bluetooth.le.ScanSettings
//import android.content.Context
//import android.content.pm.PackageManager
//import android.os.Build
//import android.os.Bundle
//import android.os.Handler
//import android.os.Looper
//import android.util.Log
//import android.view.LayoutInflater
//import android.widget.TextView
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.DialogFragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.iot_air_quality_android.R
//import com.example.iot_air_quality_android.ui.home.HomeFragment
//import java.util.concurrent.CopyOnWriteArrayList
//
//class BluetoothDeviceDialogFragment : DialogFragment() {
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var textTitle: TextView
//    private lateinit var textFound: TextView
//    private lateinit var buttonStopScan: TextView
//    private lateinit var adapter: DeviceAdapter
//
//    private var bluetoothAdapter: BluetoothAdapter? = null
//    private var scanner: BluetoothLeScanner? = null
//
//    private val handler = Handler(Looper.getMainLooper())
//    private val SCAN_TIMEOUT_MS = 10_000L
//
//    private val devices = CopyOnWriteArrayList<DeviceItem>()
//
//    // 🔹 clickHandler를 별도로 저장
//    private lateinit var clickHandler: (DeviceItem) -> Unit
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val view = LayoutInflater.from(context).inflate(R.layout.dialog_bluetooth_devices, null)
//        recyclerView = view.findViewById(R.id.recycler_devices)
//        textTitle = view.findViewById(R.id.text_title)
//        textFound = view.findViewById(R.id.text_found)
//        buttonStopScan = view.findViewById(R.id.button_stop_scan)
//
//        // 클릭 시 홈프래그먼트로 연결 상태 전달
//        clickHandler = { selected ->
//            (parentFragment as? HomeFragment)?.updateConnectionState(true, selected.name)
//            stopScan()
//            dismiss()
//        }
//
//        adapter = DeviceAdapter(devices.toList(), clickHandler)
//        recyclerView.layoutManager = LinearLayoutManager(context)
//        recyclerView.adapter = adapter
//
//        buttonStopScan.setOnClickListener {
//            stopScan()
//            dismiss()
//        }
//
//        initBluetooth()
//        startScan()
//
//        val builder = AlertDialog.Builder(requireContext())
//        builder.setView(view)
//        return builder.create()
//    }
//
//    private fun initBluetooth() {
//        val manager = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
//        bluetoothAdapter = manager.adapter
//        scanner = bluetoothAdapter?.bluetoothLeScanner
//    }
//
//    private fun startScan() {
//        if (scanner == null) return
//
//        // ✅ 런타임 권한 체크
//        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            ContextCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.BLUETOOTH_SCAN
//            ) == PackageManager.PERMISSION_GRANTED
//        } else {
//            ContextCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        }
//
//        if (!hasPermission) return
//
//        val filters = listOf(ScanFilter.Builder().build())
//        val settings = ScanSettings.Builder()
//            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
//            .build()
//
//        devices.clear()
//        updateHeader()
//
//        try {
//            scanner?.startScan(filters, settings, scanCallback)
//            handler.postDelayed({ stopScan() }, SCAN_TIMEOUT_MS)
//        } catch (e: SecurityException) {
//            e.printStackTrace()
//        }
//    }
//
//    private fun stopScan() {
//        try {
//            val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                ContextCompat.checkSelfPermission(
//                    requireContext(),
//                    Manifest.permission.BLUETOOTH_SCAN
//                ) == PackageManager.PERMISSION_GRANTED
//            } else {
//                ContextCompat.checkSelfPermission(
//                    requireContext(),
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                ) == PackageManager.PERMISSION_GRANTED
//            }
//
//            if (!hasPermission) return
//            scanner?.stopScan(scanCallback)
//        } catch (e: SecurityException) {
//            e.printStackTrace()
//        }
//
//        handler.removeCallbacksAndMessages(null)
//    }
//
//    private fun hasScanPerm(): Boolean =
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
//            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
//        else
//            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//
//    private fun hasConnectPerm(): Boolean =
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
//            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
//        else
//            true // S 미만은 CONNECT 권한 개념 없음
//
//    // ✅ 실제 BLE 스캔 콜백
//    private val scanCallback = object : ScanCallback() {
//        @SuppressLint("MissingPermission") // 아래에서 직접 권한 체크함
//        override fun onScanResult(callbackType: Int, result: ScanResult) {
//            val device = result.device ?: return
//
//            // CONNECT 권한이 있을 때만 device.name 접근
//            val safeDeviceName = if (hasConnectPerm()) device.name else null
//            val name = safeDeviceName ?: result.scanRecord?.deviceName ?: "Unknown"
//
//            if (!name.contains("Bandi-Pico", ignoreCase = true)) return
//
//            val uuidString = result.scanRecord?.serviceUuids?.firstOrNull()?.uuid?.toString()
//                ?: device.address
//
//            val item = DeviceItem(name = name, uuidOrMac = uuidString)
//            if (devices.any { it.uuidOrMac == item.uuidOrMac }) return
//
//            Log.d("BLE_SCAN", "Found: ${name}, address=${result.device.address}")
//
//            devices.add(item)
//            adapter = DeviceAdapter(devices.toList(), clickHandler)
//            recyclerView.adapter = adapter
//            updateHeader()
//        }
//
//
//        override fun onBatchScanResults(results: MutableList<ScanResult>) {
//            results.forEach { onScanResult(ScanSettings.CALLBACK_TYPE_ALL_MATCHES, it) }
//        }
//
//        override fun onScanFailed(errorCode: Int) {
//            // 필요시 로그 표시
//            Log.e("BLE_SCAN", "Scan failed: $errorCode")
//        }
//    }
//
//    private fun updateHeader() {
//        val n = devices.size
//        textTitle.text = "Available Devices ($n)"
//        textFound.text = "Found $n Device(s)"
//    }
//
//    override fun onDestroy() {
//        stopScan()
//        super.onDestroy()
//    }
//}
//
//data class DeviceItem(
//    val name: String,
//    val uuidOrMac: String
//)

package com.example.iot_air_quality_android.ui.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.iot_air_quality_android.R
import com.example.iot_air_quality_android.ui.home.HomeFragment
import java.util.concurrent.CopyOnWriteArrayList

class BluetoothDeviceDialogFragment(
    private val onDeviceSelected: (BluetoothDevice) -> Unit // ✅ 선택된 기기 전달 콜백
) : DialogFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var textTitle: TextView
    private lateinit var textFound: TextView
    private lateinit var buttonStopScan: TextView
    private lateinit var adapter: DeviceAdapter

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var scanner: BluetoothLeScanner? = null

    private val handler = Handler(Looper.getMainLooper())
    private val SCAN_TIMEOUT_MS = 10_000L

    private val devices = CopyOnWriteArrayList<BluetoothDevice>() // ✅ 실제 BluetoothDevice 저장

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_bluetooth_devices, null)
        recyclerView = view.findViewById(R.id.recycler_devices)
        textTitle = view.findViewById(R.id.text_title)
        textFound = view.findViewById(R.id.text_found)
        buttonStopScan = view.findViewById(R.id.button_stop_scan)

        adapter = DeviceAdapter(devices) { device ->
            try {
                scanner?.stopScan(scanCallback)
            } catch (e: SecurityException) {
                Log.e("BLE_SCAN", "Stop scan failed", e)
            }

            onDeviceSelected(device)
            dismiss()
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        buttonStopScan.setOnClickListener {
            stopScan()
            dismiss()
        }

        initBluetooth()
        startScan()

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(view)
        return builder.create()
    }

    // ✅ Bluetooth 초기화
    private fun initBluetooth() {
        val manager = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = manager.adapter
        scanner = bluetoothAdapter?.bluetoothLeScanner
    }

    // ✅ 스캔 시작
    @SuppressLint("MissingPermission")
    private fun startScan() {
        if (scanner == null) return

        if (!hasScanPermission()) {
            Toast.makeText(requireContext(), "Bluetooth scan permission denied.", Toast.LENGTH_SHORT).show()
            return
        }

        val filters = listOf(ScanFilter.Builder().build())
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        devices.clear()
        updateHeader()

        try {
            scanner?.startScan(filters, settings, scanCallback)
            handler.postDelayed({ stopScan() }, SCAN_TIMEOUT_MS)
        } catch (e: SecurityException) {
            Log.e("BLE_SCAN", "Failed to start scan: missing permission", e)
        }
    }

    // ✅ 스캔 중지
    @SuppressLint("MissingPermission")
    private fun stopScan() {
        try {
            if (!hasScanPermission()) return
            scanner?.stopScan(scanCallback)
        } catch (e: SecurityException) {
            Log.e("BLE_SCAN", "Failed to stop scan", e)
        }
        handler.removeCallbacksAndMessages(null)
    }

    // ✅ 권한 체크 함수
    private fun hasScanPermission(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
        else
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun hasConnectPermission(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        else true

    // ✅ BLE 스캔 콜백
    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device ?: return

            val deviceName = try {
                if (hasConnectPermission()) device.name ?: "Unknown" else "Unknown"
            } catch (_: SecurityException) { "Unknown" }

            if (!deviceName.contains("Bandi-Pico", ignoreCase = true)) return

            val address = try { device.address } catch (_: SecurityException) { null } ?: return
            if (devices.any { it.address == address }) return

            Log.d("BLE_SCAN", "Found: ${device.name}, address=${result.device.address}")

            val index = devices.size
            devices.add(device)
            adapter.notifyItemInserted(index)
            updateHeader()
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            results.forEach { onScanResult(ScanSettings.CALLBACK_TYPE_ALL_MATCHES, it) }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("BLE_SCAN", "Scan failed: $errorCode")
        }
    }

    // ✅ 상단 정보 갱신
    private fun updateHeader() {
        val n = devices.size
        textTitle.text = "Available Devices ($n)"
        textFound.text = "Found $n Device(s)"
    }

    override fun onDestroy() {
        stopScan()
        super.onDestroy()
    }
}
