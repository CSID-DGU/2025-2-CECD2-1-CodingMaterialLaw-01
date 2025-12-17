//
//  Date+Extension.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/29/25.
//

import Foundation

extension Date {
    func formattedString() -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
        formatter.timeZone = TimeZone(secondsFromGMT: 0)
/*        formatter.timeZone = TimeZone(identifier: "Asia/Seoul")*/ // 또는 UTC
        return formatter.string(from: self)
    }
}
