//
//  NetworkMonitor.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 6/3/25.
//

import Foundation
import Network

final class NetworkMonitor {
    static let shared = NetworkMonitor()

    private let monitor = NWPathMonitor()
    private let queue = DispatchQueue(label: "NetworkMonitor")

    private(set) var isConnected: Bool = false {
        didSet {
            if isConnected && oldValue == false {
                Task {
                    let token = TokenManager.shared.getAccessToken() ?? ""
                    await AirQualitySyncCoordinator().syncStoredDataIfNeeded(accessToken: token)
                }
            }
        }
    }

    private init() {
        monitor.pathUpdateHandler = { [weak self] path in
            self?.isConnected = path.status == .satisfied
            print("🌐 Network status changed: \((self?.isConnected == true) ? "connected" : "disconnected")")
        }
        monitor.start(queue: queue)
    }

    func currentStatus() -> Bool {
        return isConnected
    }
}
