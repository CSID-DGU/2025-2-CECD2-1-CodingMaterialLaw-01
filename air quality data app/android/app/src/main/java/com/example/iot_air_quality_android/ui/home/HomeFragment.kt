package com.example.iot_air_quality_android.ui.home

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.iot_air_quality_android.R
import com.example.iot_air_quality_android.ble.BleForegroundService
import com.example.iot_air_quality_android.databinding.FragmentHomeBinding
import com.example.iot_air_quality_android.ui.bluetooth.BluetoothDeviceDialogFragment
import com.example.iot_air_quality_android.util.LocationUtil
import com.example.iot_air_quality_android.viewmodel.BleSharedViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val vm: BleSharedViewModel by activityViewModels()

    private val prefs by lazy {
        requireContext().getSharedPreferences("ble_prefs", Context.MODE_PRIVATE)
    }

    private val blePermissions =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
        else
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    private val enableBluetoothLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) openBleDialog()
            else Toast.makeText(requireContext(), "Bluetooth must be turned on.", Toast.LENGTH_SHORT).show()
        }

    private val requestPerms =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.values.all { it }) checkBluetoothAndOpenDialog()
            else Toast.makeText(requireContext(), "Permissions are required to connect.", Toast.LENGTH_SHORT).show()
        }

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val prefs = requireContext().getSharedPreferences("ble_prefs", Context.MODE_PRIVATE)

        if (BleForegroundService.isRunning(requireContext())) {
            val savedName = prefs.getString("device_name", null)
            val savedMac = prefs.getString("device_mac", null)

            // ① ViewModel 복원
            vm.setConnected(savedName, savedMac, true)

            // ② 자동으로 DeviceControlFragment로 이동
            findNavController().navigate(R.id.action_home_to_deviceControl)

            return binding.root
        }

        // 버튼 연결
        binding.buttonConnect.setOnClickListener {
            if (vm.connected.value) {
                disconnectDevice()
            } else {
                requestAllPermissionsAndConnect()
            }
        }

        return binding.root
    }

    // 통합 Connect Flow
    private fun requestAllPermissionsAndConnect() {
        if (!LocationUtil.checkAndRequestPermissions(requireActivity())) return
        LocationUtil.ensureLocationServiceEnabled(requireActivity())
        requestPerms.launch(blePermissions)
    }

    private fun checkBluetoothAndOpenDialog() {
        val bluetoothManager = requireContext().getSystemService(BluetoothManager::class.java)
        val adapter = bluetoothManager?.adapter ?: return

        if (!adapter.isEnabled) {
            enableBluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        } else {
            openBleDialog()
        }
    }

    private fun openBleDialog() {
        BluetoothDeviceDialogFragment { device ->
            connectToDevice(device)
        }.show(parentFragmentManager, "ble_device_dialog")
    }

    // 실제 연결은 Service가 단독 관리
    private fun connectToDevice(device: BluetoothDevice) {
        prefs.edit().apply {
            putString("device_name", device.name)
            putString("device_mac", device.address)
            apply()
        }
        BleForegroundService.start(requireContext(), device)
        vm.setConnected(device.name, device.address, true) // UI 즉시 반영 (실제 연결 이벤트는 Service에서 브로드캐스트로도 옴)
        Toast.makeText(requireContext(), "Connecting to ${device.name ?: "device"}...", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_home_to_deviceControl)
    }

    private fun disconnectDevice() {
        BleForegroundService.stop(requireContext())
        vm.setConnected(null, null, false)
        prefs.edit().clear().apply()
        Toast.makeText(requireContext(), "Disconnected.", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
