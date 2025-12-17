//
//  AirQualityLevel.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 12/6/25.
//

import SwiftUI

enum AirQualityLevel {
    case good       // Blue
    case moderate   // Green
    case bad        // Orange
    case veryBad    // Red
    case unknown    // Gray
    
    var color: Color {
        switch self {
        case .good:     return .green
        case .moderate: return .yellow
        case .bad:      return .orange
        case .veryBad:  return .red
        case .unknown:  return .gray
        }
    }
}

extension AirQualityLevel {
    static func level(for type: SensorType, value: Double) -> AirQualityLevel {
        switch type {
        case .pm25:       return pm25Level(value)
        case .pm10:       return pm10Level(value)
        case .temperature:return temperatureLevel(value)
        case .humidity:   return humidityLevel(value)
        case .co2:        return co2Level(value)
        case .voc:        return vocLevel(value)
        }
    }
    
    private static func pm25Level(_ v: Double) -> AirQualityLevel {
        switch v {
        case 0..<15:     return .good      // Green
        case 15..<35:    return .moderate  // Yellow
        case 35..<76:    return .bad       // Orange
        default:         return .veryBad   // Red
        }
    }
    
    private static func pm10Level(_ v: Double) -> AirQualityLevel {
        switch v {
        case 0..<30:     return .good
        case 30..<80:    return .moderate
        case 80..<150:   return .bad
        default:         return .veryBad
        }
    }
    
    private static func temperatureLevel(_ v: Double) -> AirQualityLevel {
        switch v {
        case -9..<11:    return .moderate
        case 11..<31:    return .bad
        default:         return .veryBad
        }
    }
    
    private static func humidityLevel(_ v: Double) -> AirQualityLevel {
        switch v {
        case 0..<40:     return .good
        case 40..<70:    return .moderate
        default:         return .bad
        }
    }
    
    private static func co2Level(_ v: Double) -> AirQualityLevel {
        switch v {
        case 0..<1500:   return .good
        case 1500..<2500:return .moderate
        default:         return .veryBad
        }
    }
    
    private static func vocLevel(_ v: Double) -> AirQualityLevel {
        switch v {
        case 0..<250:    return .good
        case 250..<450:  return .moderate
        default:         return .bad
        }
    }
}
