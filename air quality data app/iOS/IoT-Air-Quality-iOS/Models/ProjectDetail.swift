//
//  ProjectDetailDTO.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/26/25.
//

import Foundation

struct ProjectDetail: Decodable {
    let pmEmail: String
    let projectTitle: String
    let participant: Int
    let description: String
    let projectType: String
    let startDate: String
    let endDate: String
    let createdAt: String

    // 개인정보 항목
    let email: Bool
    let gender: Bool
    let phoneNumber: Bool
    let dateOfBirth: Bool
    let bloodType: Bool
    let height: Bool
    let weight: Bool
    let name: Bool

    // 헬스 데이터 항목
    let stepCount: Bool
    let runningSpeed: Bool
    let basalEnergyBurned: Bool
    let activeEnergyBurned: Bool
    let sleepAnalysis: Bool
    let heartRate: Bool
    let oxygenSaturation: Bool
    let bloodPressureSystolic: Bool
    let bloodPressureDiastolic: Bool
    let respiratoryRate: Bool
    let bodyTemperature: Bool
    let ecgData: Bool
    let watchDeviceLatitude: Bool
    let watchDeviceLongitude: Bool

    // 공기질 데이터 항목
    let pm25Value: Bool
    let pm25Level: Bool
    let pm10Value: Bool
    let pm10Level: Bool
    let temperature: Bool
    let temperatureLevel: Bool
    let humidity: Bool
    let humidityLevel: Bool
    let co2Value: Bool
    let co2Level: Bool
    let vocValue: Bool
    let vocLevel: Bool
    let picoDeviceLatitude: Bool
    let picoDeviceLongitude: Bool
}

extension ProjectDetail {
    struct StringField {
        let label: String
        let value: String
    }

    struct BoolField {
        let label: String
        let value: Bool
    }

    var metadataFields: [StringField] {
        [
            .init(label: "Title", value: projectTitle),
            .init(label: "Project Manager Email", value: pmEmail),
            .init(label: "Number of Participants", value: "\(participant)"),
            .init(label: "Description", value: description),
            .init(label: "Project Type", value: projectType),
            .init(label: "Start Date", value: startDate),
            .init(label: "End Date", value: endDate),
            .init(label: "Created At", value: createdAt)
        ]
    }

    var personalFields: [BoolField] {
        [
            .init(label: "Email", value: email),
            .init(label: "Gender", value: gender),
            .init(label: "Phone Number", value: phoneNumber),
            .init(label: "Date of Birth", value: dateOfBirth),
            .init(label: "Blood Type", value: bloodType),
            .init(label: "Height", value: height),
            .init(label: "Weight", value: weight),
            .init(label: "Name", value: name)
        ]
    }

    var healthFields: [BoolField] {
        [
            .init(label: "Step Count", value: stepCount),
            .init(label: "Running Speed", value: runningSpeed),
            .init(label: "Basal Energy Burned", value: basalEnergyBurned),
            .init(label: "Active Energy Burned", value: activeEnergyBurned),
            .init(label: "Sleep Analysis", value: sleepAnalysis),
            .init(label: "Heart Rate", value: heartRate),
            .init(label: "Oxygen Saturation", value: oxygenSaturation),
            .init(label: "Systolic BP", value: bloodPressureSystolic),
            .init(label: "Diastolic BP", value: bloodPressureDiastolic),
            .init(label: "Respiratory Rate", value: respiratoryRate),
            .init(label: "Body Temperature", value: bodyTemperature),
            .init(label: "ECG Data", value: ecgData),
            .init(label: "Watch Latitude", value: watchDeviceLatitude),
            .init(label: "Watch Longitude", value: watchDeviceLongitude)
        ]
    }

    var airFields: [BoolField] {
        [
            .init(label: "PM2.5 Value", value: pm25Value),
            .init(label: "PM2.5 Level", value: pm25Level),
            .init(label: "PM10 Value", value: pm10Value),
            .init(label: "PM10 Level", value: pm10Level),
            .init(label: "Temperature", value: temperature),
            .init(label: "Temperature Level", value: temperatureLevel),
            .init(label: "Humidity", value: humidity),
            .init(label: "Humidity Level", value: humidityLevel),
            .init(label: "CO2 Value", value: co2Value),
            .init(label: "CO2 Level", value: co2Level),
            .init(label: "VOC Value", value: vocValue),
            .init(label: "VOC Level", value: vocLevel),
            .init(label: "Pico Latitude", value: picoDeviceLatitude),
            .init(label: "Pico Longitude", value: picoDeviceLongitude)
        ]
    }
}
