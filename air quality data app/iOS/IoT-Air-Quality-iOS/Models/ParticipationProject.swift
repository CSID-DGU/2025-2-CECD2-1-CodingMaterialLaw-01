//
//  ParticipationProject.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 12/6/25.
//

import Foundation

struct ParticipationProjectList: Decodable {
    let projects: [ParticipationProject]
}

struct ParticipationProject: Identifiable, Decodable, Hashable {
    let projectId: Int
    let title: String
    
    var id: Int { projectId }
}
