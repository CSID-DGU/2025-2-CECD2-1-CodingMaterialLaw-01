//
//  ParsedSensorData.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/29/25.
//


struct ParsedSensorData {
    let pm25: Double
    let pm10: Double
    let temperature: Double
    let humidity: Double
    let co2: Double
    let voc: Double
    let levels: [Int]
}
