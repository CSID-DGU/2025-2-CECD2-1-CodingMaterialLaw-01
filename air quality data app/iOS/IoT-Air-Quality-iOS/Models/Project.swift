//
//  Project.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/26/25.
//

import Foundation

struct Project: Codable {
    let id: Int
    let projectTitle: String
    let termsOfPolicy: String
    let privacyPolicy: String
    let healthDataConsent: String
    let airDataConsent: String
    let localDataTermsOfService: String

    enum CodingKeys: String, CodingKey {
        case id = "projectId"
        case projectTitle
        case termsOfPolicy
        case privacyPolicy
        case healthDataConsent
        case airDataConsent
        case localDataTermsOfService
    }
}
