//
//  SignupRegisterViewModel.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/27/25.
//
import Foundation

@MainActor
final class SignupRegisterViewModel: ObservableObject {
    func patchRegistration(request: SignupRegisterRequest) async -> Bool {
        do {
            let bodyData = try JSONEncoder().encode(request)

            let response: ServerResponse<JwtToken> = try await APIClient.shared.request(
                endpoint: "/api/v1/auth/register/air-quality-data",
                method: "PATCH",
                body: bodyData
            )

            if response.success, let tokens = response.data {
                TokenManager.shared.saveAccessToken(tokens.accessToken)
                TokenManager.shared.saveRefreshToken(tokens.refreshToken)
                print("✅ 회원 등록 및 토큰 갱신 성공")
                return true
            } else {
                print("❌ 회원 등록 실패 또는 토큰 없음")
                return false
            }

        } catch {
            print("❌ patchRegistration 실패: \(error)")
            return false
        }
    }
}
