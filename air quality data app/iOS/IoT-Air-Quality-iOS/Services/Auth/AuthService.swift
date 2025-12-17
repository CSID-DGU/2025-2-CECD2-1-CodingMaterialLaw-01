//
//  AuthService.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/23/25.
//

import Foundation

final class AuthService {
    static let shared = AuthService()
    
    private let baseURL = APIConstants.baseURL
    private let session = URLSession.shared

    func refreshAccessToken(completion: @escaping (Bool) -> Void) {
        guard let refreshToken = TokenManager.shared.getRefreshToken() else {
            print("❌ No refresh token found")
            completion(false)
            return
        }

        guard let url = URL(string: "\(baseURL)/api/v1/auth/refresh") else {
            completion(false)
            return
        }

        var request = URLRequest(url: url)
        request.httpMethod = "PATCH"

        // ✅ 핵심: 서버는 쿠키 기반 Refresh 처리
        request.setValue("refreshToken=\(refreshToken)", forHTTPHeaderField: "Cookie")

        // (선택) AccessToken도 같이 보낼 수 있음
//        if let accessToken = TokenManager.shared.getAccessToken() {
//            request.setValue("Bearer \(accessToken)", forHTTPHeaderField: "Authorization")
//        }

        URLSession.shared.dataTask(with: request) { data, response, error in
            guard let httpResponse = response as? HTTPURLResponse else {
                completion(false)
                return
            }

            guard (200...299).contains(httpResponse.statusCode), let data = data else {
                print("❌ Refresh failed with status: \(httpResponse.statusCode)")
                completion(false)
                return
            }

            do {
                let decoded = try JSONDecoder().decode(ServerResponse<AccessTokenOnly>.self, from: data)
                if let newAccessToken = decoded.data?.accessToken {
                    TokenManager.shared.saveAccessToken(newAccessToken)
                    print("🔄 AccessToken refreshed")

                    // 서버가 새 쿠키 내려주면 갱신
                    if let newSetCookie = httpResponse.allHeaderFields["Set-Cookie"] as? String,
                       let newRefresh = newSetCookie
                        .components(separatedBy: "refreshToken=")
                        .last?
                        .split(separator: ";")
                        .first {
                        TokenManager.shared.saveRefreshToken(String(newRefresh))
                        print("🍪 RefreshToken updated from Set-Cookie")
                    }

                    completion(true)
                } else {
                    completion(false)
                }
            } catch {
                print("❌ Decode error: \(error)")
                completion(false)
            }
        }.resume()
    }

    func exchangeAppleToken(identityToken: String, appState: AppState) async {
        guard let url = URL(string: "\(baseURL)/api/v1/auth/login/apple") else { return }

        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpBody = try? JSONSerialization.data(withJSONObject: ["identityToken": identityToken])

        let session = URLSession(configuration: .default,
                                 delegate: CustomSessionDelegate(),
                                 delegateQueue: nil)

        do {
            let (data, response) = try await session.data(for: request)

            print("🔽 서버 원본 응답:")
            print(String(data: data, encoding: .utf8) ?? "No readable data")

            if let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200 {
                let decoded = try JSONDecoder().decode(ServerResponse<TokenData>.self, from: data)

                if let tokenData = decoded.data {
                    print("✅ Apple 토큰 교환 성공")

                    TokenManager.shared.saveAccessToken(tokenData.accessToken)
                    TokenManager.shared.saveRefreshToken(tokenData.refreshToken)

                    DispatchQueue.main.async {
                        appState.isLoggedIn = true
                    }
                } else {
                    print("❌ Apple 토큰 교환 실패: data 필드가 null")
                }
            } else {
                print("❌ 서버 응답 오류: \((response as? HTTPURLResponse)?.statusCode ?? -1)")
            }
        } catch {
            print("❌ 요청 실패: \(error.localizedDescription)")
        }

    }    
}

// 로컬 테스트 용 코드 : 나중에 삭제 필요
class CustomSessionDelegate: NSObject, URLSessionDelegate {
    func urlSession(_ session: URLSession,
                    didReceive challenge: URLAuthenticationChallenge,
                    completionHandler: @escaping (URLSession.AuthChallengeDisposition, URLCredential?) -> Void) {
        if challenge.protectionSpace.authenticationMethod == NSURLAuthenticationMethodServerTrust,
           let serverTrust = challenge.protectionSpace.serverTrust {
            completionHandler(.useCredential, URLCredential(trust: serverTrust))
        } else {
            completionHandler(.performDefaultHandling, nil)
        }
    }
}
