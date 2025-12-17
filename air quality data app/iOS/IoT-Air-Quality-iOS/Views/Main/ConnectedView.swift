//
//  ConnectedView.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/29/25.
//

import SwiftUI

struct ConnectedView: View {
    @EnvironmentObject var appState: AppState
    @EnvironmentObject var feedback: FeedbackManager
    @StateObject var viewModel = PicoSensorViewModel()

    var body: some View {
        ZStack {
            VStack(spacing: 0) {
                // 상단 회색 박스
                VStack(spacing: 10) {
                    HStack(alignment: .top) {
                        VStack(alignment: .leading, spacing: 20) {
                            Text("Connected Device:")
                                .font(.headline)
                                .foregroundColor(.green)

                            Button("Disconnect") {
                                PicoBLEManager.shared.disconnect()
                                appState.isConnected = false
                                appState.connectedDeviceName = ""
                                appState.connectedDeviceMac = ""
                                appState.nextScreen = .disconnectedMain
                                
                                UserDefaults.standard.removeObject(forKey: "lastConnectedDeviceUUID")
                            }
                            .buttonStyle(.borderedProminent)
                        }

                        Spacer()

                        VStack(alignment: .trailing, spacing: 10) {
                            Text(appState.connectedDeviceName.isEmpty ? "Pico Device" : appState.connectedDeviceName)
                                .font(.body)

                            Text(appState.connectedDeviceMac.isEmpty ? "00:11:22:33:44:55" : appState.connectedDeviceMac)
                                .font(.caption)
                                .foregroundColor(.gray)
                        }
                    }
                }
                .padding()
                .frame(maxWidth: .infinity)
                .background(Color.gray.opacity(0.2))

                if let data = viewModel.latestData {
                    VStack(spacing: 20) {
                        Text("Real-Time Air Quality")
                            .font(.largeTitle)
                            .fontWeight(.bold)
                            .padding(.top, 20)

                        LazyVGrid(columns: Array(repeating: .init(.flexible()), count: 2), spacing: 16) {
                            ForEach(generateDataItems(from: data), id: \.label) { item in
                                VStack(spacing: 8) {
                                    Circle()
                                        .fill(item.levelColor)
                                        .frame(width: 16, height: 16)

                                    Text(item.label)
                                        .font(.subheadline)

                                    Text(item.value.components(separatedBy: " ")[0])
                                        .font(.title)
                                        .fontWeight(.bold)
                                        .foregroundColor(item.levelColor)

                                    Text(item.value.components(separatedBy: " ")[1])
                                        .font(.caption)
                                        .foregroundColor(.gray)
                                }
                                .frame(maxWidth: .infinity)
                                .padding()
                                .background(Color.white)
                                .cornerRadius(12)
                                .shadow(radius: 2)
                            }
                        }
                        .padding()
                    }
                } else {
                    Spacer()
                    Text("Fetching sensor data...").foregroundColor(.gray).padding()
                    Spacer()
                }

                Spacer()
            }
            GlobalFeedbackOverlay()
        }
        .onChange(of: viewModel.latestData) { newValue in
            print("🌀 View에서 데이터 수신됨:", newValue ?? "nil")
        }
        .onAppear {
            // ✅ BLE 연결 시 피드백
            PicoBLEManager.shared.onDeviceConnected = { _ in
                DispatchQueue.main.async {
                    appState.isConnected = true
                    appState.notifyBluetoothConnected()
                }
            }
        }
        .tabItem {
            Image(systemName: "house")
            Text("Home")
        }
    }

    private func generateDataItems(from data: AirQualityData) -> [(label: String, value: String, levelColor: Color)] {
        return [
            ("PM2.5", "\(data.pm25Value) µg/m³", levelColor(for: data.pm25Level)),
            ("PM10", "\(data.pm10Value) µg/m³", levelColor(for: data.pm10Level)),
            ("Temperature", "\(data.temperature) ℃", levelColor(for: data.temperatureLevel)),
            ("Humidity", "\(data.humidity) %", levelColor(for: data.humidityLevel)),
            ("CO₂", "\(data.co2Value) ppm", levelColor(for: data.co2Level)),
            ("VOC", "\(data.vocValue) ppm", levelColor(for: data.vocLevel))
        ]
    }

    private func levelColor(for level: Int) -> Color {
        switch level {
        case 0: return .green
        case 1: return .yellow
        case 2: return .orange
        case 3: return .red
        default: return .gray
        }
    }
}

#Preview {
    ConnectedView()
}
