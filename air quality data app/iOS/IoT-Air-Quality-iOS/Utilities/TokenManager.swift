//  TokenManager.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/21/25.

import Foundation
import KeychainAccess

final class TokenManager {
    static let shared = TokenManager()

    private let keychain = Keychain(service: "com.monorama.IoT-Air-Quality-iOS")

    private let accessTokenKey = "accessToken"
    private let refreshTokenKey = "refreshToken"

    // Access Token 저장
    func saveAccessToken(_ token: String) {
        do {
            try keychain.set(token, key: accessTokenKey)
        } catch {
            print("❌ accessToken 저장 실패: \(error)")
        }
    }

    // Access Token 불러오기
    func getAccessToken() -> String? {
        do {
            return try keychain.get(accessTokenKey)
        } catch {
            print("❌ accessToken 불러오기 실패: \(error)")
            return nil
        }
    }

    // Refresh Token 저장
    func saveRefreshToken(_ token: String) {
        do {
            try keychain.set(token, key: refreshTokenKey)
        } catch {
            print("❌ refreshToken 저장 실패: \(error)")
        }
    }

    // Refresh Token 불러오기
    func getRefreshToken() -> String? {
        do {
            return try keychain.get(refreshTokenKey)
        } catch {
            print("❌ refreshToken 불러오기 실패: \(error)")
            return nil
        }
    }

    // 로그아웃 시 삭제
    func clearTokens() {
        do {
            try keychain.remove(accessTokenKey)
            try keychain.remove(refreshTokenKey)
        } catch {
            print("❌ 토큰 삭제 실패: \(error)")
        }
    }
}
