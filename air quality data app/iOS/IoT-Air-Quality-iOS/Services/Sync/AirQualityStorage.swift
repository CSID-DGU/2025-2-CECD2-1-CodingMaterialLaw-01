//
//  AirQualityStorage.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 6/3/25.
//

import Foundation

class AirQualityStorage {
    private let fileName = "air_quality_data.json"

    private var fileURL: URL {
        let dir = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!
        return dir.appendingPathComponent(fileName)
    }

    func save(_ newData: AirQualityData) {
        var existing = loadAll()
        existing.append(newData)
        let dataList = AirQualityDataList(airQualityDataList: existing)
        do {
            let data = try JSONEncoder().encode(dataList)
            try data.write(to: fileURL)
            
            // ✅ 백업 제외 설정
            var filePath = fileURL
            var resourceValues = URLResourceValues()
            resourceValues.isExcludedFromBackup = true
            try filePath.setResourceValues(resourceValues)
            
        } catch {
            print("❌ 저장 실패: \(error)")
        }
    }

    func loadAll() -> [AirQualityData] {
        do {
            let data = try Data(contentsOf: fileURL)
            let decoded = try JSONDecoder().decode(AirQualityDataList.self, from: data)
            return decoded.airQualityDataList
        } catch {
            return []
        }
    }

    func clear() {
        try? FileManager.default.removeItem(at: fileURL)
    }
}
