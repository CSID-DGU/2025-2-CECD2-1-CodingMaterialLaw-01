//
//  PicoDataParser.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/29/25.
//

import Foundation

enum PicoDataParser {
    static func parseSensorData(_ data: Data) -> ParsedSensorData? {
        guard data.count == 18 else { return nil }

        func getValue(_ offset: Int) -> (Double, Int) {
            let high = Int(data[offset])
            let low = Int(data[offset + 1])
            let level = Int(data[offset + 2])
            let value = Double(high * 256 + low)
            return (value, level)
        }

        let (pm25, pm25Level) = getValue(0)
        let (pm10, pm10Level) = getValue(3)
        let (tRaw, tempLevel) = getValue(6)
        let temperature = tRaw / 10.0
        let (hRaw, humidityLevel) = getValue(9)
        let humidity = hRaw / 10.0
        let (co2, co2Level) = getValue(12)
        let (voc, vocLevel) = getValue(15)

        return ParsedSensorData(
            pm25: pm25, pm10: pm10, temperature: temperature,
            humidity: humidity, co2: co2, voc: voc,
            levels: [pm25Level, pm10Level, tempLevel, humidityLevel, co2Level, vocLevel]
        )
    }
}
