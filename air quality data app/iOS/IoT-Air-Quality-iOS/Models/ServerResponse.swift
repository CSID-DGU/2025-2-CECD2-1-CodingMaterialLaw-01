//
//  ServerResponse.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/26/25.
//


import Foundation

struct ServerResponse<T: Decodable>: Decodable {
    let success: Bool
    let data: T?
    let error: ErrorInfo?
}

struct ErrorInfo: Decodable {
    let code: Int
    let message: String
}
