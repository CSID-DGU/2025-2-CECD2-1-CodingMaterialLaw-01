//
//  FeedbackManager.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 6/11/25.
//


import SwiftUI

class FeedbackManager: ObservableObject {
    @Published var showToast: Bool = false
    @Published var toastMessage: String = ""

    @Published var showAlert: Bool = false
    @Published var alertMessage: String = ""

    func triggerToast(_ message: String) {
        toastMessage = message
        withAnimation {
            showToast = true
        }
        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
            withAnimation {
                self.showToast = false
            }
        }
    }

    func triggerAlert(_ message: String) {
        alertMessage = message
        showAlert = true
    }
}
