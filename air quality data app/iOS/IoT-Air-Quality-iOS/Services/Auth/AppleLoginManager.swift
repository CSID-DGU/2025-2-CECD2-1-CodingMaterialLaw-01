//
//  AppleLoginManager.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/23/25.
//

import Foundation
import AuthenticationServices
import SwiftUI

final class AppleLoginManager: NSObject, ObservableObject {
    private var completionHandler: ((String, String?, String?) -> Void)? // identityToken, name, email

    func startSignInWithAppleFlow(completion: @escaping (String, String?, String?) -> Void) {
        self.completionHandler = completion

        let request = ASAuthorizationAppleIDProvider().createRequest()
        request.requestedScopes = [.fullName, .email]

        let controller = ASAuthorizationController(authorizationRequests: [request])
        controller.delegate = self
        controller.presentationContextProvider = self
        controller.performRequests()
    }
}

extension AppleLoginManager: ASAuthorizationControllerDelegate {
    func authorizationController(controller: ASAuthorizationController, didCompleteWithAuthorization authorization: ASAuthorization) {
        if let credential = authorization.credential as? ASAuthorizationAppleIDCredential,
           let tokenData = credential.identityToken,
           let identityToken = String(data: tokenData, encoding: .utf8) {

            let name = [credential.fullName?.givenName, credential.fullName?.familyName]
                .compactMap { $0 }
                .joined(separator: " ")
            let email = credential.email

            print("✅ identityToken 받음: \(identityToken)")
            print("👤 사용자 이름: \(name)")
            print("📧 이메일: \(email ?? "없음")")

            completionHandler?(identityToken, name.isEmpty ? nil : name, email)
        } else {
            print("❌ identityToken 추출 실패")
            completionHandler?("", nil, nil)
        }
    }

    func authorizationController(controller: ASAuthorizationController, didCompleteWithError error: Error) {
        print("❌ Apple 로그인 실패: \(error.localizedDescription)")
        completionHandler?("", nil, nil)
    }
}

extension AppleLoginManager: ASAuthorizationControllerPresentationContextProviding {
    func presentationAnchor(for controller: ASAuthorizationController) -> ASPresentationAnchor {
        UIApplication.shared.connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap { $0.windows }
            .first { $0.isKeyWindow } ?? ASPresentationAnchor()
    }
}
