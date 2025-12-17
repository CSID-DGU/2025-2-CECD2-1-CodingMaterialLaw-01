//
//  GlobalFeedbackOverlay.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 6/11/25.
//


import SwiftUI

struct GlobalFeedbackOverlay: View {
    @EnvironmentObject var feedback: FeedbackManager

    var body: some View {
        ZStack {
            if feedback.showToast {
                toastView
                    .transition(.move(edge: .bottom).combined(with: .opacity))
                    .zIndex(1000)
            }
        }
        .ignoresSafeArea()
        .animation(.easeInOut, value: feedback.showToast)
        .alert(isPresented: $feedback.showAlert) {
            Alert(
                title: Text("알림"),
                message: Text(feedback.alertMessage),
                dismissButton: .default(Text("확인"))
            )
        }
    }

    private var toastView: some View {
        VStack {
            Spacer()

            Text(feedback.toastMessage)
                .padding(.horizontal, 24)
                .padding(.vertical, 12)
                .background(Color.black.opacity(0.85))
                .foregroundColor(.white)
                .cornerRadius(12)
                .shadow(radius: 4)
                .padding(.bottom, 120)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}
