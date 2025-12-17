import Foundation
import Combine
import CoreLocation

final class PicoSensorViewModel: ObservableObject {
    @Published var latestData: AirQualityData?

    private let locationManager = LocationManager()
    private var cancellables = Set<AnyCancellable>()
    private let syncCoordinator = AirQualitySyncCoordinator()

    init() {
        let locationPublisher = locationManager.$currentLocation
            .compactMap { $0?.coordinate }

        let sensorPublisher = PicoBLEManager.shared.dataPublisher

        Publishers.CombineLatest(locationPublisher, sensorPublisher)
            .throttle(for: .seconds(3), scheduler: RunLoop.main, latest: true)
            .sink { [weak self] (coordinate, parsed) in
                self?.handleSensorData(parsed, coordinate: coordinate)
            }
            .store(in: &cancellables)
    }

    private func handleSensorData(_ parsed: ParsedSensorData, coordinate: CLLocationCoordinate2D) {
        let data = AirQualityData(
            createdAt: Date().formattedString(),
            pm25Value: parsed.pm25,
            pm25Level: parsed.levels[0],
            pm10Value: parsed.pm10,
            pm10Level: parsed.levels[1],
            temperature: parsed.temperature,
            temperatureLevel: parsed.levels[2],
            humidity: parsed.humidity,
            humidityLevel: parsed.levels[3],
            co2Value: parsed.co2,
            co2Level: parsed.levels[4],
            vocValue: parsed.voc,
            vocLevel: parsed.levels[5],
            picoDeviceLatitude: coordinate.latitude,
            picoDeviceLongitude: coordinate.longitude
        )

        DispatchQueue.main.async {
            self.latestData = data
        }

        sendToServer(data)
    }

    private func sendToServer(_ data: AirQualityData) {
        Task {
            let accessToken = TokenManager.shared.getAccessToken() ?? ""
            await syncCoordinator.syncOrStoreRealtime(data, accessToken: accessToken)
        }
    }
}
