//
//  BluetoothDevice.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/30/25.
//

import Foundation

struct BluetoothDevice: Identifiable {
    let id = UUID()
    let name: String
    let uuid: String
}
