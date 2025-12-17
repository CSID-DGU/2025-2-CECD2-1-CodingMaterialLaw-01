//
//  AirQualityData.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/21/25.
//

import Foundation

struct AirQualityData: Codable, Equatable {
    let createdAt: String
    let pm25Value: Double
    let pm25Level: Int
    let pm10Value: Double
    let pm10Level: Int
    let temperature: Double
    let temperatureLevel: Int
    let humidity: Double
    let humidityLevel: Int
    let co2Value: Double
    let co2Level: Int
    let vocValue: Double
    let vocLevel: Int
    let picoDeviceLatitude: Double
    let picoDeviceLongitude: Double
}
