//
//  AppState.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/23/25.
//

import Foundation

enum AppScreen {
    case login
    case signUpTerms
    case signUpRegister
    case disconnectedMain
}

final class AppState: ObservableObject {
    @Published var isLoggedIn: Bool = false
    @Published var nextScreen: AppScreen = .login
    @Published var signupPrefillInfo: UserSignupInfo? = nil
    
    // 🔵 Bluetooth 연결 상태 추가
    @Published var isConnected: Bool = false
    @Published var connectedDeviceName: String = ""
    @Published var connectedDeviceMac: String = ""
    
    var feedback: FeedbackManager? = nil

    func markLoginSuccess() {
        isLoggedIn = true
        nextScreen = .disconnectedMain
        feedback?.triggerToast("Login successful")
    }

    func markSignupSuccess() {
        nextScreen = .disconnectedMain
        feedback?.triggerToast("You’ve successfully signed up")
    }

    func markLogoutSuccess() {
        isLoggedIn = false
        nextScreen = .login
        feedback?.triggerToast("You’ve safely logged out.")
    }

    func notifyLogoutFailure() {
        feedback?.triggerAlert("Logout failed. Please check your connection and try again.")
    }

    func notifyProjectJoinSuccess() {
        print("🔥 notifyProjectJoinSuccess 호출됨, feedback: \(feedback != nil)")
        feedback?.triggerToast("You have joined the project.")
    }

    func notifyProjectJoinFailure() {
        feedback?.triggerAlert("Something went wrong while joining the project.")
    }

    func notifyBluetoothConnected() {
        feedback?.triggerToast("Bluetooth connected")
    }

    func notifyBluetoothConnectionFailed() {
        feedback?.triggerAlert("Connection failed")
    }
    
    
}
