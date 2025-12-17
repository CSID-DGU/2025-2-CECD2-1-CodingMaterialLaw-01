//
//  PicoBLEManager.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/29/25.
//

import CoreBluetooth
import Combine

protocol PicoBLEManagerDelegate: AnyObject {
    func didReceiveSensorData(_ data: ParsedSensorData)
}

final class PicoBLEManager: NSObject, ObservableObject, CBCentralManagerDelegate, CBPeripheralDelegate {
    static let shared = PicoBLEManager()

    private var centralManager: CBCentralManager!
    private var picoPeripheral: CBPeripheral?
    private var discoveredPeripherals: [CBPeripheral] = []

    weak var delegate: PicoBLEManagerDelegate?

    private let sensorUUID = CBUUID(string: "0000FFB3-0000-1000-8000-00805F9B34FB")
    private let scanSubject = PassthroughSubject<[CBPeripheral], Never>()
    private let dataSubject = PassthroughSubject<ParsedSensorData, Never>()

    // ✅ 연결된 디바이스 콜백
    var onDeviceConnected: ((CBPeripheral) -> Void)?
    
    // BLE 연결 실패 콜백
    var onDeviceConnectionFailed: ((CBPeripheral?, Error?) -> Void)?

    var scanPublisher: AnyPublisher<[CBPeripheral], Never> {
        scanSubject.eraseToAnyPublisher()
    }

    var dataPublisher: AnyPublisher<ParsedSensorData, Never> {
        return dataSubject.eraseToAnyPublisher()
    }

    override init() {
        super.init()
        centralManager = CBCentralManager(delegate: self, queue: nil)
    }

    func startScanning() {
        discoveredPeripherals.removeAll()
        centralManager.scanForPeripherals(withServices: nil, options: nil)
    }

    func stopScanning() {
        centralManager.stopScan()
    }

    func connectToPeripheral(with macAddress: String) {
        guard let peripheral = discoveredPeripherals.first(where: {
            $0.identifier.uuidString == macAddress
        }) else {
            print("🔍 해당 MAC 주소의 기기를 찾을 수 없음")
            return
        }

        picoPeripheral = peripheral
        centralManager.connect(peripheral, options: nil)
    }

    func disconnect() {
        if let peripheral = picoPeripheral {
            centralManager.cancelPeripheralConnection(peripheral)
            picoPeripheral = nil
        }
    }

    func centralManagerDidUpdateState(_ central: CBCentralManager) {
        if central.state == .poweredOn {
            startScanning()
        }
    }

    func centralManager(_ central: CBCentralManager, didDiscover peripheral: CBPeripheral, advertisementData: [String : Any], rssi RSSI: NSNumber) {
        guard let name = peripheral.name, name.contains("Bandi-Pico") else { return }

        if discoveredPeripherals.contains(where: { $0.identifier == peripheral.identifier }) {
            return
        }

        discoveredPeripherals.append(peripheral)
        scanSubject.send(discoveredPeripherals)

        // 자동 연결은 이전 연결 기기일 경우에만 수행
        let lastUUID = UserDefaults.standard.string(forKey: "lastConnectedDeviceUUID")
        if peripheral.identifier.uuidString == lastUUID {
            picoPeripheral = peripheral
            central.stopScan()
            central.connect(peripheral, options: nil)
        }
    }

    func centralManager(_ central: CBCentralManager, didConnect peripheral: CBPeripheral) {
        print("✅ BLE 연결됨: \(peripheral.name ?? "이름 없음")")
        picoPeripheral = peripheral

        // 저장
        UserDefaults.standard.set(peripheral.identifier.uuidString, forKey: "lastConnectedDeviceUUID")

        // 연결된 후 peripheral 설정
        peripheral.delegate = self
        peripheral.discoverServices(nil)

        // 연결 콜백 호출
        DispatchQueue.main.async {
            self.onDeviceConnected?(peripheral)
        }
    }
    
    func centralManager(_ central: CBCentralManager, didFailToConnect peripheral: CBPeripheral, error: Error?) {
        print("❌ BLE 연결 실패: \(error?.localizedDescription ?? "알 수 없음")")
        onDeviceConnectionFailed?(peripheral, error)
    }

    func peripheral(_ peripheral: CBPeripheral, didDiscoverServices error: Error?) {
        if let error = error {
            print("❌ 서비스 탐색 실패: \(error.localizedDescription)")
        } else {
            print("🔍 서비스 발견됨: \(peripheral.services?.count ?? 0)")
        }

        guard let services = peripheral.services else { return }
        for service in services {
            print("➡️ 서비스 UUID: \(service.uuid)")
            peripheral.discoverCharacteristics(nil, for: service)
        }
    }

    func peripheral(_ peripheral: CBPeripheral, didDiscoverCharacteristicsFor service: CBService, error: Error?) {
        if let error = error {
            print("❌ 특성 탐색 실패: \(error.localizedDescription)")
        }

        guard let characteristics = service.characteristics else { return }

        for char in characteristics {
            print("📡 특성 UUID: \(char.uuid)")
            
            switch char.uuid {
            case CBUUID(string: "0000FFE1-0000-1000-8000-00805F9B34FB"),
                 CBUUID(string: "0000FFD1-0000-1000-8000-00805F9B34FB"),
                 CBUUID(string: "0000FFC1-0000-1000-8000-00805F9B34FB"):
                let enableValue = Data([0x01])
                peripheral.writeValue(enableValue, for: char, type: .withResponse)
                print("✅ 센서 활성화 명령 전송됨: \(char.uuid)")

            case sensorUUID:
                peripheral.setNotifyValue(true, for: char)
                print("🔔 Notify 설정됨: \(char.uuid)")

            default:
                break
            }
        }
    }

    func peripheral(_ peripheral: CBPeripheral, didUpdateValueFor characteristic: CBCharacteristic, error: Error?) {
        guard let data = characteristic.value else {
            print("⚠️ characteristic.value 없음")
            return
        }

        print("📥 BLE 원시 데이터 수신: \(data)")

        if let parsed = PicoDataParser.parseSensorData(data) {
            print("✅ 파싱 성공:", parsed)
            delegate?.didReceiveSensorData(parsed)
            dataSubject.send(parsed)
        } else {
            print("❌ 파싱 실패. 원시 데이터:", data)
        }
    }
}
