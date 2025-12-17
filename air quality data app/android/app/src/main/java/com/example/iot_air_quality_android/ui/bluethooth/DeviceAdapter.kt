//package com.example.iot_air_quality_android.ui.bluetooth
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.iot_air_quality_android.R
//
//class DeviceAdapter(
//    private val devices: List<DeviceItem>,
//    val onClick: (DeviceItem) -> Unit
//) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {
//
//    inner class DeviceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val textName: TextView = view.findViewById(R.id.text_device_name)
//        val textUuid: TextView = view.findViewById(R.id.text_device_address)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_device, parent, false)
//        return DeviceViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
//        val device = devices[position]
//        holder.textName.text = device.name
//        holder.textUuid.text = device.uuidOrMac
//        holder.itemView.setOnClickListener { onClick(device) }
//    }
//
//    override fun getItemCount(): Int = devices.size
//}

package com.example.iot_air_quality_android.ui.bluetooth

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.iot_air_quality_android.R
import android.bluetooth.BluetoothDevice

class DeviceAdapter(
    private val devices: MutableList<BluetoothDevice>,
    private val onClick: (BluetoothDevice) -> Unit
) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    inner class DeviceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textName: TextView = view.findViewById(R.id.text_device_name)
        val textAddress: TextView = view.findViewById(R.id.text_device_address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device, parent, false)
        return DeviceViewHolder(view)
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = devices[position]
        holder.textName.text = device.name ?: "Unknown"
        holder.textAddress.text = device.address ?: ""
        holder.itemView.setOnClickListener { onClick(device) }
    }

    override fun getItemCount(): Int = devices.size
}
