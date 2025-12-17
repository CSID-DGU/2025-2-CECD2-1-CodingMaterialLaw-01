//
//  SignupTermsService.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/26/25.
//
// 안쓰고 있음 25-10-24

import Foundation

final class SignupTermsService {
    static let shared = SignupTermsService()
    
    private let baseURL = APIConstants.baseURL
    
    func fetchTerms(accessToken: String, completion: @escaping ([Term]?) -> Void) {
        guard let url = URL(string: "\(baseURL)/api/v1/air-quality-data/terms") else {
            completion(nil)
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("Bearer \(accessToken)", forHTTPHeaderField: "Authorization")
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            guard let httpResponse = response as? HTTPURLResponse,
                  httpResponse.statusCode == 200,
                  let data = data else {
                print("❌ 약관 조회 실패: \(error?.localizedDescription ?? "unknown error")")
                completion(nil)
                return
            }
            
            do {
                let decoded = try JSONDecoder().decode(ServerResponse<TermsContainer>.self, from: data)
                if let wrapper = decoded.data {
                    completion(wrapper.terms)
                } else {
                    print("❌ 약관 데이터 없음 (null)")
                    completion(nil)
                }
            } catch {
                print("❌ 약관 디코딩 실패: \(error)")
                completion(nil)
            }
        }.resume()
    }
}
