//
//  HistoryView.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 12/6/25.
//

import SwiftUI

struct HistoryView: View {
    @ObservedObject var viewModel: HistoryViewModel
    
    @State private var isProjectMenuOpen = false
    @State private var isShowingDatePicker = false
    @State private var tempDate = Date()
    
    var body: some View {
        NavigationStack {
            VStack(spacing: 16) {
                projectSelector
                dateSelector
                
                
                
                contentArea
                
                Spacer(minLength: 0)
            }
            .padding(.horizontal, 16)
            .padding(.top, 20)
            .task {
                await viewModel.loadProjectsIfNeeded()
            }
            .sheet(isPresented: $isShowingDatePicker) {
                datePickerSheet
            }
        }
    }
    
    // MARK: - 상단: 프로젝트 선택
    
    private var projectSelector: some View {
        Menu {
            if viewModel.isLoadingProjects {
                ProgressView()
            } else {
                ForEach(viewModel.projects) { project in
                    Button(project.title) {
                        Task {
                            await viewModel.onProjectSelected(project)
                        }
                    }
                }
            }
        } label: {
            HStack {
                Text(viewModel.selectedProject?.title ?? "Select Project")
                    .foregroundColor(viewModel.selectedProject == nil ? .secondary : .primary)
                Spacer()
                Image(systemName: "chevron.down")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 10)
            .background(
                RoundedRectangle(cornerRadius: 10)
                    .stroke(Color.gray.opacity(0.3), lineWidth: 1)
            )
        }
    }
    
    // MARK: - 상단: 날짜 선택
    
    private var dateSelector: some View {
        Button {
            tempDate = viewModel.selectedDate ?? Date()
            isShowingDatePicker = true
        } label: {
            HStack {
                Text(viewModel.selectedDateText())
                    .foregroundColor(viewModel.selectedDate == nil ? .secondary : .primary)
                Spacer()
                Image(systemName: "calendar")
                    .foregroundColor(.secondary)
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 10)
            .background(
                RoundedRectangle(cornerRadius: 10)
                    .stroke(Color.gray.opacity(0.3), lineWidth: 1)
            )
        }
    }
    
    // MARK: - 본문 영역
    
    @ViewBuilder
    private var contentArea: some View {
        if viewModel.selectedProject == nil || viewModel.selectedDate == nil {
            placeholderCard(text: "Select project and date to see your Air Quality data history")
        }
        else if viewModel.isLoadingHistory && viewModel.items.isEmpty {
            VStack {
                Spacer()
                ProgressView("Loading history...")
                Spacer()
            }
        }
        else if viewModel.hasSearched && viewModel.items.isEmpty {
            placeholderCard(text: "No records found for this date.")
        }
        else {
            ScrollView {
                LazyVStack(spacing: 16) {
                    ForEach(viewModel.items) { item in
                        HistoryCard(item: item)
                            .onAppear {
                                Task {
                                    await viewModel.loadNextPageIfNeeded(currentItem: item)
                                }
                            }
                    }
                    
                    if viewModel.isLoadingHistory && !viewModel.items.isEmpty {
                        ProgressView()
                            .padding(.vertical, 16)
                    }
                }
                .padding(.vertical, 8)
            }
        }
    }
   
    private func placeholderCard(text: String) -> some View {
        VStack {
            Spacer()
            Text(text)
                .multilineTextAlignment(.center)
                .foregroundColor(.secondary)
                .padding()
                .frame(maxWidth: .infinity)
                .background(
                    RoundedRectangle(cornerRadius: 16)
                        .fill(Color(.systemGray6))
                )
            Spacer()
        }
    }
    
    // MARK: - DatePicker Sheet
    
    private var datePickerSheet: some View {
        NavigationStack {
            VStack {
                DatePicker(
                    "",
                    selection: $tempDate,
                    displayedComponents: .date
                )
                .datePickerStyle(.graphical)
                .padding()
                
                Spacer()
            }
            .navigationTitle("Select Date")
            .toolbar {
                ToolbarItem(placement: .confirmationAction) {
                    Button("Done") {
                        Task {
                            await viewModel.onDateSelected(tempDate)
                        }
                        isShowingDatePicker = false
                    }
                }
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") {
                        isShowingDatePicker = false
                    }
                }
            }
        }
    }
}
