//
//  MetricCell.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 12/6/25.
//

import SwiftUI

struct MetricCell: View {
    let title: String
    let valueText: String
    let level: AirQualityLevel
    
    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(title)
                .font(.caption)
                .foregroundColor(.secondary)
            
            HStack {
                Text(valueText)
                    .font(.subheadline)
                    .fontWeight(.semibold)
                
                Spacer()
                
                Rectangle()
                    .fill(level.color)
                    .frame(width: 10, height: 10)
                    .cornerRadius(2)
            }
        }
        .padding(10)
        .background(
            RoundedRectangle(cornerRadius: 12)
                .fill(Color(.systemGray6))
        )
    }
}
