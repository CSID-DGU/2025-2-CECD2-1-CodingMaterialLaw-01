//
//  HistoryViewModel.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 12/6/25.
//

import Foundation

@MainActor
final class HistoryViewModel: ObservableObject {
    
    @Published var projects: [ParticipationProject] = []
    @Published var isLoadingProjects = false

    @Published var selectedProject: ParticipationProject?
    @Published var selectedDate: Date?
    
    @Published var items: [HistoryItem] = []
    @Published var isLoadingHistory = false
    @Published var errorMessage: String?
    @Published var hasNext = false
    @Published var hasSearched = false
    
    private var page = 0
    private let pageSize = 50
    
    // MARK: - 프로젝트 목록
    
    func loadProjectsIfNeeded() async {
        guard projects.isEmpty, !isLoadingProjects else { return }
        isLoadingProjects = true
        errorMessage = nil
        
        do {
            let list = try await HistoryService.fetchParticipationProjects()
            self.projects = list
        } catch let apiError as APIError {
            self.errorMessage = Self.mapAPIError(apiError)
        } catch {
            self.errorMessage = error.localizedDescription
        }
        
        isLoadingProjects = false
    }
    
    // MARK: - 선택 변경
    
    func onProjectSelected(_ project: ParticipationProject) async {
        selectedProject = project
        await reloadForCurrentSelection()
    }
    
    func onDateSelected(_ date: Date) async {
        selectedDate = date
        await reloadForCurrentSelection()
    }
    
    // MARK: - 히스토리 조회
    
    private func reloadForCurrentSelection() async {
        guard let project = selectedProject,
              let date = selectedDate else {
            return
        }
        await loadFirstPage(projectId: project.projectId, date: date)
    }
    
    private func loadFirstPage(projectId: Int, date: Date) async {
        guard !isLoadingHistory else { return }
        isLoadingHistory = true
        errorMessage = nil
        hasSearched = true
        page = 0
        
        do {
            let result = try await HistoryService.fetchHistory(
                projectId: projectId,
                date: Self.apiDateFormatter.string(from: date),
                page: page,
                size: pageSize
            )
            self.items = result.historyList
            self.hasNext = result.hasNext
        } catch let apiError as APIError {
            self.errorMessage = Self.mapAPIError(apiError)
            self.items = []
            self.hasNext = false
        } catch {
            self.errorMessage = error.localizedDescription
            self.items = []
            self.hasNext = false
        }
        
        isLoadingHistory = false
    }
    
    func loadNextPageIfNeeded(currentItem item: HistoryItem?) async {
        guard let item,
              !isLoadingHistory,
              hasNext,
              let project = selectedProject,
              let date = selectedDate else { return }
        
        let thresholdIndex = items.index(items.endIndex, offsetBy: -5, limitedBy: items.startIndex)
            ?? items.startIndex
        
        if let index = items.firstIndex(of: item), index >= thresholdIndex {
            await loadNextPage(projectId: project.projectId, date: date)
        }
    }
    
    private func loadNextPage(projectId: Int, date: Date) async {
        guard !isLoadingHistory, hasNext else { return }
        isLoadingHistory = true
        errorMessage = nil
        page += 1
        
        do {
            let result = try await HistoryService.fetchHistory(
                projectId: projectId,
                date: Self.apiDateFormatter.string(from: date),
                page: page,
                size: pageSize
            )
            self.items.append(contentsOf: result.historyList)
            self.hasNext = result.hasNext
        } catch let apiError as APIError {
            self.errorMessage = Self.mapAPIError(apiError)
            self.hasNext = false
            self.page -= 1
        } catch {
            self.errorMessage = error.localizedDescription
            self.hasNext = false
            self.page -= 1
        }
        
        isLoadingHistory = false
    }
    
    // MARK: - 유틸
    
    private static let apiDateFormatter: DateFormatter = {
        let f = DateFormatter()
        f.locale = Locale(identifier: "en_US_POSIX")
        f.timeZone = TimeZone(identifier: "Asia/Seoul")
        f.dateFormat = "yyyy-MM-dd"
        return f
    }()
    
    func selectedDateText() -> String {
        guard let date = selectedDate else { return "Select Date" }
        return Self.apiDateFormatter.string(from: date)
    }
    
    private static func mapAPIError(_ error: APIError) -> String {
        switch error {
        case .invalidURL:
            return "잘못된 요청입니다."
        case .requestFailed(let statusCode):
            return "요청이 실패했습니다. (코드 \(statusCode))"
        case .decodingFailed:
            return "데이터를 해석하는 데 실패했습니다."
        case .unauthorized:
            return "인증이 만료되었습니다. 다시 로그인해주세요."
        case .unknown(let err):
            return err.localizedDescription
        }
    }
}
