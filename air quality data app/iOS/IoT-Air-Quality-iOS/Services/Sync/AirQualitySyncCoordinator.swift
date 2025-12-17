//
//  AirQualitySyncCoordinator.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 6/3/25.
//

import Foundation

final class AirQualitySyncCoordinator {
    private let storage = AirQualityStorage()

    // ✅ 실시간 전송 or 로컬 저장
    func syncOrStoreRealtime(_ data: AirQualityData, accessToken: String) async {
        if NetworkMonitor.shared.currentStatus() {
            do {
                try await AirQualityAPIService.shared.sendSensorData(data)
                print("✅ realtime 전송 성공")
            } catch {
                print("❌ realtime 전송 실패, 로컬 저장")
                storage.save(data)
            }
        } else {
            print("📴 네트워크 꺼짐 → 로컬 저장")
            storage.save(data)
        }
    }

    // ✅ 저장된 데이터만 일괄 전송
    func syncStoredDataIfNeeded(accessToken: String) async {
        guard NetworkMonitor.shared.currentStatus() else { return }

        let stored = storage.loadAll()
        let count = stored.count
        guard !stored.isEmpty else { return }

        print("📦 저장된 데이터 \(count)개를 서버에 sync 시도")

        do {
            try await AirQualitySyncService().sync(stored)
            storage.clear()
            print("✅ 저장 데이터 sync 완료")
        } catch {
            print("❌ 저장 데이터 sync 실패: \(error)")
        }
    }
}
