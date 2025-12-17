//
//  APIClient.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/21/25.
//

import Foundation

enum APIError: Error {
    case invalidURL
    case requestFailed(statusCode: Int)
    case decodingFailed
    case unauthorized
    case unknown(Error)
}

final class APIClient {
    static let shared = APIClient()
    private init() {}

    private let baseURL = APIConstants.baseURL

    func request<T: Decodable>(
        endpoint: String,
        method: String = "GET",
        body: Data? = nil,
        headers: [String: String] = [:],
        useAuth: Bool = true,
        retry: Bool = true
    ) async throws -> T {
        guard let url = URL(string: "\(baseURL)\(endpoint)") else {
            throw APIError.invalidURL
        }

        var request = URLRequest(url: url)
        request.httpMethod = method
        request.httpBody = body
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")

        // ✅ 최신 AccessToken 주입
        if useAuth, let token = TokenManager.shared.getAccessToken() {
            request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        }

        headers.forEach { key, value in
            request.setValue(value, forHTTPHeaderField: key)
        }

        do {
            let (data, response) = try await URLSession.shared.data(for: request)
            guard let httpResponse = response as? HTTPURLResponse else {
                throw APIError.requestFailed(statusCode: -1)
            }

            // ✅ 401 처리
            if httpResponse.statusCode == 401 && retry {
                print("⚠️ Access token expired, attempting refresh...")

                let refreshed = await withCheckedContinuation { continuation in
                    AuthService.shared.refreshAccessToken { success in
                        continuation.resume(returning: success)
                    }
                }

                if refreshed {
                    print("✅ Token refresh succeeded, retrying original request")

                    // ✅ 새 토큰으로 Authorization 갱신 후 재시도
                    var retriedRequest = request
                    if let newToken = TokenManager.shared.getAccessToken() {
                        retriedRequest.setValue("Bearer \(newToken)", forHTTPHeaderField: "Authorization")
                    }

                    let (data2, response2) = try await URLSession.shared.data(for: retriedRequest)
                    guard let httpResponse2 = response2 as? HTTPURLResponse,
                          (200...299).contains(httpResponse2.statusCode) else {
                        throw APIError.requestFailed(statusCode: (response2 as? HTTPURLResponse)?.statusCode ?? -1)
                    }

                    do {
                        return try JSONDecoder().decode(T.self, from: data2)
                    } catch {
                        throw APIError.decodingFailed
                    }
                } else {
                    print("❌ Token refresh failed — forcing logout")
                    TokenManager.shared.clearTokens()
                    throw APIError.unauthorized
                }
            }

            // ✅ 정상 응답
            guard (200...299).contains(httpResponse.statusCode) else {
                throw APIError.requestFailed(statusCode: httpResponse.statusCode)
            }

            return try JSONDecoder().decode(T.self, from: data)

        } catch {
            throw APIError.unknown(error)
        }
    }
}
