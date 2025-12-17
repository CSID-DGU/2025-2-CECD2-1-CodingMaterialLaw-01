import SwiftUI

struct HistoryCard: View {
    let item: HistoryItem
    
    private let columns = [
        GridItem(.flexible(), spacing: 8),
        GridItem(.flexible(), spacing: 8)
    ]
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(item.createdAt)
                .font(.headline)
            
            LazyVGrid(columns: columns, spacing: 8) {
                MetricCell(
                    title: "PM2.5",
                    valueText: String(format: "%.1f µg/m³", item.pm25Value),
                    level: AirQualityLevel.level(for: .pm25, value: item.pm25Value)
                )

                MetricCell(
                    title: "PM10",
                    valueText: String(format: "%.1f µg/m³", item.pm10Value),
                    level: AirQualityLevel.level(for: .pm10, value: item.pm10Value)
                )

                MetricCell(
                    title: "Temperature",
                    valueText: String(format: "%.1f ℃", item.temperature),
                    level: AirQualityLevel.level(for: .temperature, value: item.temperature)
                )

                MetricCell(
                    title: "Humidity",
                    valueText: String(format: "%.1f %%", item.humidity),
                    level: AirQualityLevel.level(for: .humidity, value: item.humidity)
                )

                MetricCell(
                    title: "CO₂",
                    valueText: String(format: "%.1f ppm", item.co2Value),
                    level: AirQualityLevel.level(for: .co2, value: item.co2Value)
                )

                MetricCell(
                    title: "VOC",
                    valueText: String(format: "%.1f ppb", item.vocValue),
                    level: AirQualityLevel.level(for: .voc, value: item.vocValue)
                )
            }
        }
        .padding(12)
        .background(
            RoundedRectangle(cornerRadius: 16)
                .fill(Color(.systemBackground))
                .shadow(color: Color.black.opacity(0.05), radius: 4, x: 0, y: 2)
        )
    }
}
