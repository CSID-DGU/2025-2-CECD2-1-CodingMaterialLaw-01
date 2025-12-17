//
//  AirQualityEndpoints.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/21/25.
//

import Foundation

enum HTTPMethod: String {
    case GET
    case POST
    case PATCH
}

enum AirQualityEndpoints {
    case fetchTerms
    case registerUserProfile
    case fetchProjects
    case joinProject(projectId: Int)
    case postRealtimeData
    case postSyncData

    var path: String {
        switch self {
        case .fetchTerms:
            return "/api/v1/air-quality-data/terms"
        case .registerUserProfile:
            return "/api/v1/air-quality-data/user"
        case .fetchProjects:
            return "/api/v1/air-quality-data/projects"
        case .joinProject(let projectId):
            return "/api/v1/projects/\(projectId)/participation"
        case .postRealtimeData:
            return "/api/v1/air-quality-data/realtime"
        case .postSyncData:
            return "/api/v1/air-quality-data/sync"
        }
    }

    var method: HTTPMethod {
        switch self {
        case .fetchTerms, .fetchProjects:
            return .GET
        case .registerUserProfile, .postRealtimeData, .postSyncData, .joinProject:
            return .POST
        }
    }
}
