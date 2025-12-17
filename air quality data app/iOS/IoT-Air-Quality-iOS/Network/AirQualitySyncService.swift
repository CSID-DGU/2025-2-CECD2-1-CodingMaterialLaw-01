//
//  AirQualitySyncService.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/29/25.
//

import Foundation

protocol AirQualitySyncEncoder {
    func encode(_ dataList: AirQualityDataList) throws -> Data
}

struct JSONSyncEncoder: AirQualitySyncEncoder {
    func encode(_ dataList: AirQualityDataList) throws -> Data {
        return try JSONEncoder().encode(dataList)
    }
}

final class AirQualitySyncService {
    private let encoder: AirQualitySyncEncoder

    init(encoder: AirQualitySyncEncoder = JSONSyncEncoder()) {
        self.encoder = encoder
    }

    /// 공기질 데이터 서버 동기화 (POST /api/v1/air-quality-data/sync)
    func sync(_ data: [AirQualityData]) async throws {
        let dataList = AirQualityDataList(airQualityDataList: data)
        let body = try encoder.encode(dataList)

        // ✅ APIClient 사용: accessToken은 자동으로 Bearer 헤더 포함됨
        let response: ServerResponse<EmptyData> = try await APIClient.shared.request(
            endpoint: "/api/v1/air-quality-data/sync",
            method: "POST",
            body: body
        )

        // ✅ 서버의 success 필드로 검증
        guard response.success else {
            print("❌ Sync 실패: 서버에서 success=false 반환")
            throw URLError(.badServerResponse)
        }

        print("✅ Sync 성공: 데이터 \(data.count)개 업로드 완료")
    }
}
