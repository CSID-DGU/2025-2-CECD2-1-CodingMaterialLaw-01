//
//  SignupTermsViewModel.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/26/25.
//

import Foundation

final class SignupTermsViewModel: ObservableObject {
    @Published var terms: [Term?] = [nil, nil] // 2개 고정
    @Published var agreements: [Bool] = Array(repeating: false, count: 2)
    
    private let baseURL = APIConstants.baseURL

    var allAgreed: Bool {
        agreements.allSatisfy { $0 }
    }

    func loadTerms() async {
        do {
            let response: ServerResponse<TermsContainer> = try await APIClient.shared.request(
                endpoint: "/api/v1/auth/air-quality-data/terms",
                method: "GET",
                useAuth: false
            )
            
            if let fetchedTerms = response.data?.terms {
                for term in fetchedTerms {
                    switch term.type {
                    case "privacy-policy": self.terms[0] = term
                    case "terms-of-service": self.terms[1] = term
                    default: break
                    }
                }
            } else {
                print("❌ 약관 로딩 실패: data is nil")
            }
        } catch {
            print("❌ loadTerms 실패: \(error)")
        }
    }

    func resetAgreements() {
        agreements = Array(repeating: false, count: 2)
    }

    func titleFor(index: Int) -> String {
        switch index {
        case 0: return "Privacy Policy"
        case 1: return "Terms of Service"
        default: return ""
        }
    }

    func contentFor(index: Int) -> String {
        terms[index]?.content ?? "Sorry, we couldn’t load the content."
    }
    
    func determineNextScreen(appState: AppState) {
        guard let accessToken = TokenManager.shared.getAccessToken(),
              let claims = JWTDecoder.decode(token: accessToken),
              let role = claims.role else { return }

        switch role {
        case "GUEST":
            appState.nextScreen = .signUpRegister
            
        case "HD_USER":
            Task {
                let success = await registerAirQualityRole()
                if success {
                    appState.markSignupSuccess()
                    appState.nextScreen = .disconnectedMain
                } else {
                    appState.feedback?.triggerAlert("Role registration failed.")
                }
            }
            
        case "PM":
            appState.nextScreen = .disconnectedMain
            
        default:
            break
        }
    }
    
    private func registerAirQualityRole() async -> Bool {
        do {
            let response: ServerResponse<JwtToken> = try await APIClient.shared.request(
                endpoint: "/api/v1/auth/register/air-quality-data/role",
                method: "PATCH",
                body: "{}".data(using: .utf8)
            )
            
            if response.success, let tokens = response.data {
                TokenManager.shared.saveAccessToken(tokens.accessToken)
                TokenManager.shared.saveRefreshToken(tokens.refreshToken)
                print("🟢 역할 업데이트 성공")
                return true
            } else {
                print("❌ 역할 업데이트 실패: \(response.error?.message ?? "unknown error")")
                return false
            }
        } catch {
            print("❌ registerAirQualityRole 실패: \(error)")
            return false
        }
    }
}
