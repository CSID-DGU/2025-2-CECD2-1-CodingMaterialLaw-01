//
//  DisconnectedView.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/27/25.
//

import SwiftUI
import Combine

struct DisconnectedView: View {
    @EnvironmentObject var appState: AppState

    @State private var showModal = false
    @State private var isProjectJoined = false
    @State private var availableDevices: [BluetoothDevice] = []
    @State private var isScanning = false
    @State private var cancellable: AnyCancellable?

    var body: some View {
        ZStack {
            VStack(spacing: 0) {
                // 상단 회색 박스
                VStack(alignment: .leading, spacing: 20) {
                    Text("Disconnect")
                        .font(.headline)
                        .foregroundColor(.red)
                    Button("Connect") {
                        startScanning()
                    }
                    .buttonStyle(.borderedProminent)
                }
                .padding()
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(Color.gray.opacity(0.2))

                Spacer()

                VStack(spacing: 12) {
                    if isProjectJoined {
                        Text("No Bluetooth device connected")
                            .font(.title2)
                        Text("Connect your Bluetooth device by the 'Connect' button")
                            .font(.subheadline)
                            .foregroundColor(.gray)
                    } else {
                        Text("Please join the project")
                            .font(.title2)
                        Text("You can join a project in 'Setting'")
                            .font(.subheadline)
                            .foregroundColor(.gray)
                    }
                }

                Spacer()
            }
            .onAppear {
                // ✅ 블루투스 연결 성공 시
                PicoBLEManager.shared.onDeviceConnected = { _ in
                    DispatchQueue.main.async {
                        appState.isConnected = true
                        appState.notifyBluetoothConnected()
                    }
                }

                // ✅ 블루투스 연결 실패 시
                PicoBLEManager.shared.onDeviceConnectionFailed = { _, _ in
                    DispatchQueue.main.async {
                        appState.notifyBluetoothConnectionFailed()
                    }
                }
            }

            GlobalFeedbackOverlay()
        }
        .tabItem {
            Image(systemName: "house")
            Text("Home")
        }
        .sheet(isPresented: $showModal) {
            VStack(spacing: 16) {
                Text("Available Devices (\(availableDevices.count))")
                    .font(.headline)
                Divider()

                Text("Found \(availableDevices.count) Device(s)")
                    .font(.subheadline)

                if isScanning {
                    Spacer()
                    ProgressView("Searching Devices...")
                        .progressViewStyle(CircularProgressViewStyle())
                    Spacer()
                } else {
                    ForEach(availableDevices) { device in
                        Button(action: {
                            connectToDevice(device)
                        }) {
                            VStack(alignment: .leading) {
                                Text(device.name)
                                    .font(.body)
                                Text(device.uuid)
                                    .font(.caption)
                                    .foregroundColor(.gray)
                            }
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding()
                            .background(Color.blue.opacity(0.1))
                            .cornerRadius(8)
                        }
                    }
                }

                Spacer()
                HStack {
                    Spacer()
                    Button("Stop Scan") {
                        stopScanning()
                    }
                }
                .padding()
            }
            .padding()
        }
    }

    private func startScanning() {
        isScanning = true
        showModal = true

        PicoBLEManager.shared.startScanning()

        cancellable = PicoBLEManager.shared.scanPublisher
            .receive(on: DispatchQueue.main)
            .sink { peripherals in
                self.availableDevices = peripherals.map {
                    BluetoothDevice(name: $0.name ?? "Unknown", uuid: $0.identifier.uuidString)
                }
                self.isScanning = false
            }
    }

    private func stopScanning() {
        showModal = false
        isScanning = false
        availableDevices = []
        PicoBLEManager.shared.stopScanning()
        cancellable?.cancel()
    }

    private func connectToDevice(_ device: BluetoothDevice) {
        print("Connecting to \(device.name)...")
        PicoBLEManager.shared.connectToPeripheral(with: device.uuid)
        showModal = false
    }
}

#Preview {
    DisconnectedView()
        .environmentObject(AppState())
}
