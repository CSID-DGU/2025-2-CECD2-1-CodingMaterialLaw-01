//
//  JWTDecoder.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/26/25.
//
import Foundation

struct JWTClaims: Decodable {
    let uid: String?
    let role: String?
    let iat: Int?
    let exp: Int?
    
    enum CodingKeys: String, CodingKey {
        case uid
        case role = "rol"
        case iat
        case exp
    }
}

struct JWTDecoder {
    static func decode(token: String) -> JWTClaims? {
        let segments = token.split(separator: ".")
        guard segments.count == 3 else { return nil }

        var base64 = String(segments[1])
            .replacingOccurrences(of: "-", with: "+")
            .replacingOccurrences(of: "_", with: "/")

        while base64.count % 4 != 0 {
            base64 += "="
        }

        guard let payloadData = Data(base64Encoded: base64),
              let claims = try? JSONDecoder().decode(JWTClaims.self, from: payloadData) else {
            return nil
        }

        return claims
    }
}
