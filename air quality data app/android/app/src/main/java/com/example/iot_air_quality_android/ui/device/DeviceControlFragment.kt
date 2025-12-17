//package com.example.iot_air_quality_android.ui.device
//
//import android.annotation.SuppressLint
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.activityViewModels
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.lifecycleScope
//import androidx.lifecycle.repeatOnLifecycle
//import androidx.localbroadcastmanager.content.LocalBroadcastManager
//import com.example.iot_air_quality_android.R
//import com.example.iot_air_quality_android.data.model.request.SensorDataRequest
//import com.example.iot_air_quality_android.databinding.FragmentDeviceControlBinding
//import com.example.iot_air_quality_android.databinding.ViewSensorCardBinding
//import com.example.iot_air_quality_android.ui.home.HomeFragment
//import com.example.iot_air_quality_android.viewmodel.BleSharedViewModel
//import kotlinx.coroutines.flow.collectLatest
//import kotlinx.coroutines.launch
//
//class DeviceControlFragment : Fragment() {
//
//    private var _binding: FragmentDeviceControlBinding? = null
//    private val binding get() = _binding!!
//
//    private val vm: BleSharedViewModel by activityViewModels()
//
//    // ✅ BLE 데이터 수신용 브로드캐스트 리시버
//    private val bleReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//            if (intent?.action == "BLE_SENSOR_DATA") {
//                val data = intent.getParcelableExtra<SensorDataRequest>("sensor_data")
//                if (data != null) {
//                    // ✅ 메인 스레드로 ViewModel 업데이트 보장
//                    viewLifecycleOwner.lifecycleScope.launch {
//                        vm.updateSensorData(data)
//                    }
//                    Log.d("BleReceiver", "📲 Received data: $data")
//                }
//            }
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//
//        _binding = FragmentDeviceControlBinding.inflate(inflater, container, false)
//        setupHeader()
//        observeSensorData()
//        setupDisconnectButton()
//        return binding.root
//    }
//
//
//    override fun onResume() {
//        super.onResume()
//        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
//            bleReceiver,
//            IntentFilter("BLE_SENSOR_DATA")
//        )
//    }
//
//    override fun onPause() {
//        super.onPause()
//        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(bleReceiver)
//    }
//
//    // -----------------------------
//    // 상단: 연결 정보 표시
//    // -----------------------------
//    private fun setupHeader() {
//        lifecycleScope.launch {
//            vm.deviceName.collectLatest { name ->
//                binding.textDeviceName.text = name ?: "-"
//            }
//        }
//
//        lifecycleScope.launch {
//            vm.deviceMac.collectLatest { mac ->
//                binding.textMac.text = mac ?: "-"
//            }
//        }
//
//        lifecycleScope.launch {
//            vm.connected.collectLatest { connected ->
//                if (connected) {
//                    binding.textConnLabel.text = "Connected Device:"
//                    binding.textConnLabel.setTextColor(
//                        ContextCompat.getColor(requireContext(), R.color.level_good)
//                    )
//                    binding.buttonConnect.text = "Disconnect"
//                } else {
//                    binding.textConnLabel.text = "Disconnected"
//                    binding.textConnLabel.setTextColor(
//                        ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark)
//                    )
//                    binding.buttonConnect.text = "Connect"
//                }
//            }
//        }
//    }
//
//    // -----------------------------
//    // 실시간 센서 데이터 표시
//    // -----------------------------
//    private fun observeSensorData() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                vm.sensorData.collectLatest { data ->
//                    if (data != null) {
//                        Log.d("DeviceUI", "🟢 UI update triggered with data: $data")
//                        updateSensorCards(data)
//                    }
//                }
//            }
//        }
//    }
//
//    @SuppressLint("SetTextI18n")
//    private fun updateSensorCards(data: SensorDataRequest) {
//        setCard(binding.cardPm25, "PM2.5", data.pm25Value, "µg/m³", data.pm25Level)
//        setCard(binding.cardPm10, "PM10", data.pm10Value, "µg/m³", data.pm10Level)
//        setCard(binding.cardTemp, "Temperature", data.temperature, "°C", data.temperatureLevel)
//        setCard(binding.cardHum, "Humidity", data.humidity, "%", data.humidityLevel)
//        setCard(binding.cardCo2, "CO₂", data.co2Value, "ppm", data.co2Level)
//        setCard(binding.cardVoc, "VOC", data.vocValue, "ppm", data.vocLevel)
//    }
//
//    @SuppressLint("SetTextI18n")
//    private fun setCard(
//        cardView: ViewSensorCardBinding,
//        title: String,
//        value: Double,
//        unit: String,
//        level: Int
//    ) {
//        cardView.textTitle.text = title
//        cardView.textValue.text = String.format("%.1f", value)
//        cardView.textUnit.text = unit
//
//        val color = getLevelColor(level, title)
//        cardView.dot.setColorFilter(color)
//        cardView.textValue.setTextColor(color)
//    }
//
//    // -----------------------------
//    // 색상 계산
//    // -----------------------------
//    private fun getLevelColor(level: Int, key: String): Int {
//        val c = requireContext()
//        return when (key) {
//            "Temperature" -> when {
//                level <= 1 -> ContextCompat.getColor(c, R.color.level_good)
//                level == 2 -> ContextCompat.getColor(c, R.color.level_moderate)
//                level == 3 -> ContextCompat.getColor(c, R.color.level_bad)
//                else -> ContextCompat.getColor(c, R.color.level_verybad)
//            }
//            "Humidity" -> when {
//                level <= 1 -> ContextCompat.getColor(c, R.color.level_good)
//                level == 2 -> ContextCompat.getColor(c, R.color.level_moderate)
//                level == 3 -> ContextCompat.getColor(c, R.color.level_bad)
//                else -> ContextCompat.getColor(c, R.color.level_verybad)
//            }
//            else -> when (level) {
//                0, 1 -> ContextCompat.getColor(c, R.color.level_good)
//                2 -> ContextCompat.getColor(c, R.color.level_moderate)
//                3 -> ContextCompat.getColor(c, R.color.level_bad)
//                else -> ContextCompat.getColor(c, R.color.level_verybad)
//            }
//        }
//    }
//
//    // -----------------------------
//    // Disconnect 버튼
//    // -----------------------------
//    private fun setupDisconnectButton() {
//        binding.buttonConnect.setOnClickListener {
//            if (vm.connected.value) {
//                (parentFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
//                    ?.childFragmentManager
//                    ?.fragments
//                    ?.firstOrNull { it is HomeFragment } as? HomeFragment
//                        )?.let { home ->
//                        home.requireActivity().runOnUiThread {
//                            home.javaClass.getDeclaredMethod("disconnectDevice").invoke(home)
//                        }
//                    }
//                vm.setConnected(null, null, false)
//            } else {
//                requireActivity().onBackPressedDispatcher.onBackPressed()
//            }
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}

package com.example.iot_air_quality_android.ui.device

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.activity.addCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.iot_air_quality_android.R
import com.example.iot_air_quality_android.ble.BleForegroundService
import com.example.iot_air_quality_android.data.model.request.SensorDataRequest
import com.example.iot_air_quality_android.databinding.FragmentDeviceControlBinding
import com.example.iot_air_quality_android.databinding.ViewSensorCardBinding
import com.example.iot_air_quality_android.viewmodel.BleSharedViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DeviceControlFragment : Fragment() {

    private var _binding: FragmentDeviceControlBinding? = null
    private val binding get() = _binding!!

    private val vm: BleSharedViewModel by activityViewModels()

    // 센서 데이터 수신
    private val bleReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "BLE_SENSOR_DATA" -> {
                    val data = intent.getParcelableExtra<SensorDataRequest>("sensor_data")
                    if (data != null) {
                        viewLifecycleOwner.lifecycleScope.launch {
                            vm.updateSensorData(data)
                        }
                        Log.d("BleReceiver", "📲 Received data: $data")
                    }
                }
                // 연결 상태 브로드캐스트 (Service/BleManager 쪽에서 보냄)
                "BLE_CONNECTION" -> {
                    val connected = intent.getBooleanExtra("connected", false)
                    val name = intent.getStringExtra("name")
                    val mac = intent.getStringExtra("mac")
                    vm.setConnected(name, mac, connected)
                    Log.d("BleReceiver", "🔌 Connection broadcast: connected=$connected name=$name mac=$mac")
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeviceControlBinding.inflate(inflater, container, false)

        binding.layoutLoading.visibility = View.VISIBLE
        binding.gridMetrics.visibility = View.GONE

        setupHeader()
        observeSensorData()
        setupConnectButton()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (vm.connected.value) {
                requireActivity().moveTaskToBack(true)
            } else {
                isEnabled = false
                requireActivity().onBackPressed()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter().apply {
            addAction("BLE_SENSOR_DATA")
            addAction("BLE_CONNECTION")
        }
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(bleReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(bleReceiver)
    }

    private fun setupHeader() {
        lifecycleScope.launch {
            vm.deviceName.collectLatest { name ->
                binding.textDeviceName.text = name ?: "-"
            }
        }
        lifecycleScope.launch {
            vm.deviceMac.collectLatest { mac ->
                binding.textMac.text = mac ?: "-"
            }
        }
        lifecycleScope.launch {
            vm.connected.collectLatest { connected ->
                if (connected) {
                    binding.textConnLabel.text = "Connected Device:"
                    binding.textConnLabel.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.level_good)
                    )
                    binding.buttonConnect.text = "Disconnect"
                } else {
                    binding.textConnLabel.text = "Disconnected"
                    binding.textConnLabel.setTextColor(
                        ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark)
                    )
                    binding.buttonConnect.text = "Connect"
                }
            }
        }
    }

    private fun observeSensorData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.sensorData.collectLatest { data ->
                    if (data != null) {
                        binding.layoutLoading.visibility = View.GONE
                        binding.gridMetrics.visibility = View.VISIBLE

                        Log.d("DeviceUI", "🟢 UI update triggered with data: $data")
                        updateSensorCards(data)
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateSensorCards(data: SensorDataRequest) {
        setCard(binding.cardPm25, "PM2.5", data.pm25Value, "µg/m³", data.pm25Level)
        setCard(binding.cardPm10, "PM10", data.pm10Value, "µg/m³", data.pm10Level)
        setCard(binding.cardTemp, "Temperature", data.temperature, "°C", data.temperatureLevel)
        setCard(binding.cardHum, "Humidity", data.humidity, "%", data.humidityLevel)
        setCard(binding.cardCo2, "CO₂", data.co2Value, "ppm", data.co2Level)
        setCard(binding.cardVoc, "VOC", data.vocValue, "ppm", data.vocLevel)
    }

    @SuppressLint("SetTextI18n")
    private fun setCard(
        cardView: ViewSensorCardBinding,
        title: String,
        value: Double,
        unit: String,
        level: Int
    ) {
        cardView.textTitle.text = title
        cardView.textValue.text = String.format("%.1f", value)
        cardView.textUnit.text = unit

        val color = getLevelColor(level, title)
        cardView.dot.setColorFilter(color)
        cardView.textValue.setTextColor(color)
    }

    private fun getLevelColor(level: Int, key: String): Int {
        val c = requireContext()
        return when (key) {
            "Temperature" -> when {
                level <= 1 -> ContextCompat.getColor(c, R.color.level_good)
                level == 2 -> ContextCompat.getColor(c, R.color.level_moderate)
                level == 3 -> ContextCompat.getColor(c, R.color.level_bad)
                else -> ContextCompat.getColor(c, R.color.level_verybad)
            }
            "Humidity" -> when {
                level <= 1 -> ContextCompat.getColor(c, R.color.level_good)
                level == 2 -> ContextCompat.getColor(c, R.color.level_moderate)
                level == 3 -> ContextCompat.getColor(c, R.color.level_bad)
                else -> ContextCompat.getColor(c, R.color.level_verybad)
            }
            else -> when (level) {
                0, 1 -> ContextCompat.getColor(c, R.color.level_good)
                2 -> ContextCompat.getColor(c, R.color.level_moderate)
                3 -> ContextCompat.getColor(c, R.color.level_bad)
                else -> ContextCompat.getColor(c, R.color.level_verybad)
            }
        }
    }

    // Connect/Disconnect 버튼: Service 단독 제어
    private fun setupConnectButton() {
        binding.buttonConnect.setOnClickListener {
            if (vm.connected.value) {
                // ① 블루투스 연결 종료
                BleForegroundService.stop(requireContext())

                // ② ViewModel 상태 갱신
                vm.setConnected(null, null, false)

                // ③ HomeFragment로 즉시 이동
                requireActivity().runOnUiThread {
                    requireActivity().supportFragmentManager.popBackStack()
                }
                // 또는 Navigation Component 사용 시:
                // findNavController().popBackStack(R.id.homeFragment, false)
            } else {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
