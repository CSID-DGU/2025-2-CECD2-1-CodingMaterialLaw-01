//
//  AirQualityAPIService.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/29/25.
//

import Foundation

final class AirQualityAPIService {
    static let shared = AirQualityAPIService()
    private init() {}

    /// 실시간 센서 데이터 전송 (POST /api/v1/air-quality-data/realtime)
    func sendSensorData(_ data: AirQualityData) async throws {
        let body = try JSONEncoder().encode(data)

        // ✅ APIClient 사용: accessToken 자동 첨부
        let response: ServerResponse<EmptyData> = try await APIClient.shared.request(
            endpoint: "/api/v1/air-quality-data/realtime",
            method: "POST",
            body: body
        )

        guard response.success else {
            print("⚠️ 전송 실패: 서버 success=false")
            throw URLError(.badServerResponse)
        }

        print("✅ 실시간 데이터 전송 성공")
    }
}
