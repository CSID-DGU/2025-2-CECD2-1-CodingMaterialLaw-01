//
//  LoginView.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/23/25.
//


import SwiftUI
import AuthenticationServices

struct LoginView: View {
    @StateObject private var viewModel = LoginViewModel()
    @EnvironmentObject var appState: AppState

    var body: some View {
        VStack {
            Spacer(minLength: 60)

            Text("Air Quality Collection")
                .font(.largeTitle)
                .fontWeight(.semibold)
                .padding(.bottom, 80)

            Image("app_logo")
                .resizable()
                .scaledToFit()
                .frame(width: 200, height: 200)
                .padding(.bottom, 10)

            Spacer()

            VStack(spacing: 16) {
                Button(action: {
                    viewModel.handleAppleLogin(appState: appState)
                }) {
                    HStack {
                        Spacer()
                        Image(systemName: "apple.logo")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 22, height: 22)
                            .foregroundColor(.black)
                        Spacer()
                        Text("Continue with Apple")
                            .font(.title3)
                            .fontWeight(.semibold)
                            .foregroundColor(.black)
                        Spacer()
                    }
                    .padding()
                    .frame(maxWidth: .infinity)
                    .background(Color.white)
                    .overlay(
                        RoundedRectangle(cornerRadius: 45)
                            .stroke(Color.black.opacity(0.4), lineWidth: 3)
                    )
                    .cornerRadius(45)
                }
                .disabled(viewModel.isLoading)

                if viewModel.isLoading {
                    ProgressView("Logging in...")
                        .padding(.top)
                }

                if let errorMessage = viewModel.errorMessage {
                    Text(errorMessage)
                        .foregroundColor(.red)
                        .padding(.top, 8)
                }
            }
            .padding(.horizontal)

            Spacer(minLength: 40)
        }
        .background(Color(.systemBackground))
        .ignoresSafeArea()
    }
}


#Preview {
    LoginView()
}
