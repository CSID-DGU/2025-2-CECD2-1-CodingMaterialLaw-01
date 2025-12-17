//
//  TokenData.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/26/25.
//


import Foundation

struct TokenData: Decodable {
    let accessToken: String
    let refreshToken: String
}
