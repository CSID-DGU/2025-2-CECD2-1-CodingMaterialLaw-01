//
//  TokenResponse.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/23/25.
//


struct TokenResponse: Codable {
    let success: Bool
    let data: JwtToken?
    let error: String?
}
