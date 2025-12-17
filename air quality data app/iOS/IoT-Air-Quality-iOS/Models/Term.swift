//
//  Terms.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/26/25.
//

import Foundation

struct Term: Decodable {
    let type: String
    let title: String
    let content: String
}

struct TermsContainer: Decodable {
    let terms: [Term]
}
