//
//  HistoryService.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 12/6/25.
//

import Foundation

enum HistoryService {
    
    // MARK: - 참여중인 프로젝트 목록 조회
    static func fetchParticipationProjects() async throws -> [ParticipationProject] {
        let response: ServerResponse<ParticipationProjectList> =
            try await APIClient.shared.request(
                endpoint: "/api/v1/air-quality-data/projects/participation",
                method: "GET"
            )
      
        guard response.success, let data = response.data else {
            let message = response.error?.message ?? "참여 중인 프로젝트를 불러오지 못했습니다."
            throw APIError.unknown(NSError(domain: "HistoryService",
                                           code: -1,
                                           userInfo: [NSLocalizedDescriptionKey: message]))
        }
        
        return data.projects
    }
    
    
    
    static func fetchHistory(
        projectId: Int,
        date: String,
        page: Int,
        size: Int
    ) async throws -> HistoryItemList {
        
        let endpoint =
        "/api/v1/air-quality-data/history" +
        "?projectId=\(projectId)&date=\(date)&page=\(page)&size=\(size)"
        
        let response: ServerResponse<HistoryItemList> =
            try await APIClient.shared.request(
                endpoint: endpoint,
                method: "GET"
            )
        
        guard response.success, let data = response.data else {
            let message = response.error?.message ?? "히스토리 데이터를 불러오지 못했습니다."
            throw APIError.unknown(NSError(domain: "HistoryService",
                                           code: -2,
                                           userInfo: [NSLocalizedDescriptionKey: message]))
        }
        
        return data
    }
}
