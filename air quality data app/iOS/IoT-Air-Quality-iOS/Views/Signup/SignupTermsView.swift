//
//  SignupTermsView.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/23/25.
//

import SwiftUI

struct SignupTermsView: View {
    @StateObject private var viewModel = SignupTermsViewModel()
    @State private var showingTermIndex: IdentifiableInt? = nil
    @EnvironmentObject var appState: AppState

    var body: some View {
        VStack(spacing: 40) {

            Text("Sign up")
                .font(.largeTitle)
                .fontWeight(.bold)
                .padding(.top, 50)

            Image("app_logo")
                .resizable()
                .scaledToFit()
                .frame(width: 120, height: 120)

            Spacer(minLength: 200)

            VStack(alignment: .leading, spacing: 10) {
                ForEach(0..<2, id: \.self) { index in
                    HStack {
                        Text(viewModel.titleFor(index: index))
                        Spacer()
                        Button("[View]") {
                            showingTermIndex = IdentifiableInt(value: index)
                        }
                        Toggle("", isOn: $viewModel.agreements[index])
                            .labelsHidden()
                    }
                }
            }
            .padding(.horizontal)

            HStack(spacing: 16) {
                Button(action: {
                    viewModel.resetAgreements()
                }) {
                    Text("Reset")
                        .font(.title)
                        .frame(maxWidth: .infinity, minHeight: 55)
                }
                .buttonStyle(.bordered)

                Button(action: {
                    viewModel.determineNextScreen(appState: appState)
                }) {
                    Text("Next")
                        .font(.title)
                        .frame(maxWidth: .infinity, minHeight: 55)
                }
                .buttonStyle(.borderedProminent)
                .disabled(!viewModel.allAgreed)
            }
            .padding(.horizontal)

            Spacer(minLength: 20)
        }
        .sheet(item: $showingTermIndex) { identifiable in
            ScrollView {
                if viewModel.terms[identifiable.value] == nil {
                    VStack {
                        Spacer(minLength: 100)
                        ProgressView("Loading...")
                        Spacer()
                    }
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else {
                    Text(viewModel.contentFor(index: identifiable.value))
                        .padding()
                }
            }
        }
        .onAppear {
            Task {
                await viewModel.loadTerms()
            }
            
        }
    }
}

struct IdentifiableInt: Identifiable {
    let value: Int
    var id: Int { value }
}

#Preview {
    SignupTermsView()
}
