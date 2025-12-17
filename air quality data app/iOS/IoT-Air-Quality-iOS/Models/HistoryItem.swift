//
//  HistoryItem.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 12/6/25.
//

import Foundation

struct HistoryItemList: Decodable {
    let historyList: [HistoryItem]
    let hasNext: Bool
}

struct HistoryItem: Identifiable, Decodable, Hashable {
    var id: String { createdAt }
        
    let createdAt: String
    let pm25Value: Double
    let pm10Value: Double
    let temperature: Double
    let humidity: Double
    let vocValue: Double
    let co2Value: Double
    let picoDeviceLatitude: Double
    let picoDeviceLongitude: Double
}
