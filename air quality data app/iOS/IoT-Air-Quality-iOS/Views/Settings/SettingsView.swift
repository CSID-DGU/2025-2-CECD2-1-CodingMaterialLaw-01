//
//  SettingsView.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/28/25.
//

import SwiftUI

struct SettingsView: View {

    @EnvironmentObject var appState: AppState
    @StateObject private var viewModel = SettingsViewModel()

    @State private var selectedProjectId: Int? = nil
    @State private var showProjectInfo = false
    @State private var showingTermIndex: IdentifiableInt? = nil
    @State private var showLogoutAlert = false
    @State private var showConfirmAlert = false

    let termsTitles = [
        "Privacy Policy",
        "Terms of Service",
        "Consent of Health",
        "Consent of Air Data",
        "Location Data Terms of Service"
    ]

    var body: some View {
        ZStack {
            settingsContent
                .task {
                    await viewModel.fetchProjects()
                    if let first = viewModel.projectList.first {
                        selectedProjectId = first.id
                    }
                    viewModel.loadTerms()
                }

            GlobalFeedbackOverlay()
        }
    }

    var settingsContent: some View {
        VStack(spacing: 16) {
            // 로그아웃 버튼
            HStack {
                Spacer()
                Button("Logout") {
                    showLogoutAlert = true
                }
                .padding(EdgeInsets(top: 10, leading: 0, bottom: 0, trailing: 15))
                .foregroundColor(.red)
                .alert("Are you sure you want to log out?", isPresented: $showLogoutAlert) {
                    Button("No", role: .cancel) { }
                    Button("Yes", role: .destructive) {
                        TokenManager.shared.clearTokens()
                        showConfirmAlert = true
                    }
                }.alert("You’ve safely logged out.", isPresented: $showConfirmAlert) {
                    Button("OK") {
                        appState.markLogoutSuccess()
                    }
                }
            }

            // 로고 및 드롭다운
            Image("app_logo")
                .resizable()
                .scaledToFit()
                .frame(width: 100, height: 100)

            Spacer()

            Text("Project Selection")
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.leading, 30)

            HStack {
                Picker("Select a project", selection: $selectedProjectId) {
                    ForEach(viewModel.projectList, id: \.id) { project in
                        Text(project.projectTitle).tag(project.id as Int?)
                    }
                }
                .pickerStyle(.menu)
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.leading, 20)

                Button(action: {
                    if let id = selectedProjectId {
                        Task {
                            await viewModel.fetchProjectDetail(projectId: id)
                            showProjectInfo = true
                        }
                    }
                }) {
                    Image(systemName: "info.circle")
                }
                .padding(.trailing, 20)
            }

            // Kibana / Metadata 버튼
            VStack(spacing: 16) {
                Button(action: {
                    if let url = URL(string: "https://kibana.monodatum.io") {
                        UIApplication.shared.open(url)
                    }
                }) {
                    HStack {
                        Image(systemName: "link")
                        Text("View data on Web")
                    }
                }
                .buttonStyle(.bordered)

                Button(action: {
                    if let url = URL(string: "https://metadata.monodatum.io") {
                        UIApplication.shared.open(url)
                    }
                }) {
                    HStack {
                        Image(systemName: "link")
                        Text("Input Meta Data on Web")
                    }
                }
                .buttonStyle(.bordered)
            }
            .padding()

            // 약관 및 버튼
            VStack(alignment: .leading, spacing: 10) {
                ForEach(0..<5, id: \.self) { index in
                    HStack {
                        Text(termsTitles[index])
                        Spacer()
                        Button("[View]") {
                            showingTermIndex = IdentifiableInt(value: index)
                        }
                        Toggle("", isOn: $viewModel.agreements[index])
                            .labelsHidden()
                            .disabled(false)
                        // 프로젝트 등록 여부에 따라 달라져야 함. 현재 임시처리
//                            .disabled(viewModel.getProjectInfo(for: selectedProject) != nil)
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
                // 프로젝트 등록 여부에 따라 달라져야 함. 현재 임시처리
//                .disabled(viewModel.getProjectInfo(for: selectedProject) != nil)

                Button(action: {
                    if let id = selectedProjectId {
                        Task {
                            await viewModel.participateInProject(projectId: id, appState: appState)
                        }
                    }
                }) {
                    // + 눌렀을 때 DisconnectedView의 isProjectJoined가 바뀌어야 함.
                    // 프로젝트 등록 여부에 따라 달라져야 함. 현재 임시처리
//                    Text(viewModel.getProjectInfo(for: selectedProject) != nil ? "Registered" : "Register")
                    Text("Register")
                        .font(.title)
                        .frame(maxWidth: .infinity, minHeight: 55)
                }
                .buttonStyle(.borderedProminent)
                // 프로젝트 등록 여부에 따라 달라져야 함. 현재 임시처리
                .disabled(!viewModel.allAgreed /*|| viewModel.getProjectInfo(for: selectedProject) != nil*/)
            }
            .padding(.horizontal)

            Spacer()
        }
        .sheet(item: $showingTermIndex) { identifiable in
            ScrollView {
                Text(viewModel.contentFor(index: identifiable.value))
                    .padding()
            }
        }
        .sheet(isPresented: $showProjectInfo) {
            if let info = viewModel.selectedProjectDetail {
                ScrollView {
                    VStack(alignment: .leading, spacing: 20) {
                        // Metadata Section
                        Text("📌 Project Information")
                            .font(.title2).bold()

                        ForEach(info.metadataFields, id: \.label) { field in
                            HStack {
                                Text(field.label)
                                Spacer()
                                Text(field.value)
                                    .foregroundColor(.secondary)
                            }
                        }

                        Divider()

                        // Personal Info Section
                        Text("🔐 Personal Information").bold()

                        ForEach(info.personalFields, id: \.label) { field in
                            HStack {
                                Text(field.label)
                                Spacer()
                                Image(systemName: field.value ? "checkmark.square" : "square")
                                    .foregroundColor(.blue)
                            }
                        }

                        Divider()

                        // Health Data Section
                        Text("💓 Health Data").bold()

                        ForEach(info.healthFields, id: \.label) { field in
                            HStack {
                                Text(field.label)
                                Spacer()
                                Image(systemName: field.value ? "checkmark.square" : "square")
                                    .foregroundColor(.blue)
                            }
                        }

                        Divider()

                        // Air Quality Data Section
                        Text("🌫️ Air Quality Data").bold()

                        ForEach(info.airFields, id: \.label) { field in
                            HStack {
                                Text(field.label)
                                Spacer()
                                Image(systemName: field.value ? "checkmark.square" : "square")
                                    .foregroundColor(.blue)
                            }
                        }
                    }
                    .padding()
                }
            } else {
                ProgressView()
            }
        }
    }
}

struct AirQualitySettingsView_Previews: PreviewProvider {
    static var previews: some View {
        SettingsView()
            .environmentObject(AppState())
    }
}
