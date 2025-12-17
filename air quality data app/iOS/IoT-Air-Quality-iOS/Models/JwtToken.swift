//
//  JwtTokenDto.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/27/25.
//


struct JwtToken: Codable {
    let accessToken: String
    let refreshToken: String
}
