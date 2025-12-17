//
//  RootView.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/26/25.
//

import SwiftUI

struct RootView: View {
    @EnvironmentObject var appState: AppState

    var body: some View {
        switch appState.nextScreen {
        case .login:
            LoginView()
        case .signUpTerms:
            SignupTermsView()
        case .signUpRegister:
            SignupRegisterView(prefillInfo: appState.signupPrefillInfo)
        case .disconnectedMain:
            MainTabView()
        }
    }
}
