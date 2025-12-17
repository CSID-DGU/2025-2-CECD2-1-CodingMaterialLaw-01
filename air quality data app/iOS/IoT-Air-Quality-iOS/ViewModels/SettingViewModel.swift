//
//  SettingViewModel.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/28/25.
//
//

import Foundation
import Combine

struct EmptyData: Decodable {}

final class SettingsViewModel: ObservableObject {
    private let baseURL = APIConstants.baseURL
    
    @Published var projectList: [Project] = []
    @Published var agreements: [Bool] = Array(repeating: false, count: 5)
    @Published var terms: [Int: String] = [:]
    @Published var selectedProjectDetail: ProjectDetail?

    var allAgreed: Bool {
        agreements.allSatisfy { $0 }
    }

    func loadTerms() {
        for i in 0..<5 {
            terms[i] = "Sample content for term \(i + 1)."
        }
    }

    func contentFor(index: Int) -> String {
        return terms[index] ?? ""
    }

    func resetAgreements() {
        agreements = Array(repeating: false, count: 5)
    }

    func getProjectInfo(for title: String) -> Project? {
        return projectList.first(where: { $0.projectTitle == title })
    }
    
    func fetchProjects() async {
        do {
            let response: ServerResponse<ProjectListData> = try await APIClient.shared.request(
                endpoint: "/api/v1/air-quality-data/projects",
                method: "GET"
            )

            if let data = response.data {
                self.projectList = data.projectList
                print("✅ 불러온 프로젝트 목록:")
                projectList.forEach { print("• \($0.projectTitle)") }
            } else {
                print("❌ 프로젝트 목록 없음: data is nil")
            }

        } catch {
            print("❌ fetchProjects 실패: \(error)")
        }
    }

    func participateInProject(projectId: Int, appState: AppState) async {
        do {
            let response: ServerResponse<EmptyData> = try await APIClient.shared.request(
                endpoint: "/api/v1/air-quality-data/projects/\(projectId)/participation",
                method: "POST"
            )

            if response.success {
                print("✅ 프로젝트 참여 성공")
                appState.notifyProjectJoinSuccess()
            } else {
                print("❌ 서버에서 참여 실패 응답 반환")
                appState.notifyProjectJoinFailure()
            }

        } catch {
            print("❌ participateInProject 실패: \(error)")
            appState.notifyProjectJoinFailure()
        }
    }
    
    func fetchProjectDetail(projectId: Int) async {
        do {
            let response: ServerResponse<ProjectDetail> = try await APIClient.shared.request(
                endpoint: "/api/v1/air-quality-data/projects/\(projectId)",
                method: "GET"
            )

            self.selectedProjectDetail = response.data
            print("✅ 프로젝트 상세정보 불러옴: \(response.data?.projectTitle ?? "알 수 없음")")

        } catch {
            print("❌ fetchProjectDetail 실패: \(error)")
        }
    }
}
