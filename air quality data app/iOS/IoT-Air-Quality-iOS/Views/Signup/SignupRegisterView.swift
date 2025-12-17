//
//  SignupRegisterView.swift
//  IoT-Air-Quality-iOS
//
//  Created by HyungJun Lee on 5/27/25.
//

import SwiftUI

enum Gender: String, CaseIterable, Identifiable {
    case male = "MALE"
    case female = "FEMALE"
    case other = "OTHER"

    var id: String { self.rawValue }
}

enum BloodType: String, CaseIterable, Identifiable {
    case a_plus = "A+"
    case b_plus = "B+"
    case ab_plus = "AB+"
    case o_plus = "O+"
    case a_minus = "A-"
    case b_minus = "B-"
    case ab_minus = "AB-"
    case o_minus = "O-"
    case unknown = "Unknown"

    var id: String { self.rawValue }

    var serverValue: String {
        switch self {
        case .a_plus: return "A_PLUS"
        case .b_plus: return "B_PLUS"
        case .ab_plus: return "AB_PLUS"
        case .o_plus: return "O_PLUS"
        case .a_minus: return "A_MINUS"
        case .b_minus: return "B_MINUS"
        case .ab_minus: return "AB_MINUS"
        case .o_minus: return "O_MINUS"
        case .unknown: return "UNKNOWN"
        }
    }
}

enum CountryCode: String, CaseIterable, Identifiable {
    case KR = "KR"
    case US = "US"

    var id: String { rawValue }

    var displayCode: String {
        switch self {
        case .KR: return "+82"
        case .US: return "+1"
        }
    }
}

struct SignupRegisterView: View {
    @StateObject private var viewModel = SignupRegisterViewModel()
    @EnvironmentObject var appState: AppState
    @EnvironmentObject var feedback: FeedbackManager
    
    @State private var name: String = ""
    @State private var birthDate = Date()
    @State private var gender: Gender = .male
    @State private var bloodType: BloodType = .unknown
    @State private var height: String = ""
    @State private var weight: String = ""
    @State private var email: String = ""
    @State private var countryCode: CountryCode = .US
    @State private var phoneNumber: String = ""
    @State private var showBloodTypePicker = false
    @State private var showCountryCodePicker = false
    
    init(prefillInfo: UserSignupInfo? = nil) {
        _email = State(initialValue: prefillInfo?.email ?? "")
        _name = State(initialValue: prefillInfo?.name ?? "")
    }
    
    var body: some View {
        VStack(spacing: 20) {
            Text("Sign up")
                .font(.largeTitle)
                .fontWeight(.bold)
                .padding(.top, 50)
            
            Image("app_logo")
                .resizable()
                .scaledToFit()
                .frame(width: 120, height: 120)
            
            VStack(spacing: 7) {
                InputRow(title: "Birth Date") {
                    DatePicker("", selection: $birthDate, displayedComponents: .date)
                        .labelsHidden()
                        .frame(maxWidth: .infinity, alignment: .trailing)
                }
                InputRow(title: "Gender") {
                    Menu {
                        ForEach(Gender.allCases) { value in
                            Button(value.rawValue) { gender = value }
                        }
                    } label: {
                        Text(gender.rawValue)
                            .frame(maxWidth: .infinity, alignment: .trailing)
                    }
                }
                InputRow(title: "Blood Type") {
                    Button(action: {
                        showBloodTypePicker.toggle()
                    }) {
                        Text(bloodType.rawValue)
                            .frame(maxWidth: .infinity, alignment: .trailing)
                    }
                }
                .sheet(isPresented: $showBloodTypePicker) {
                    VStack {
                        Picker("", selection: $bloodType) {
                            ForEach(BloodType.allCases) { type in
                                Text(type.rawValue).tag(type)
                            }
                        }
                        .labelsHidden()
                        .pickerStyle(WheelPickerStyle())
                        Button("Done") {
                            showBloodTypePicker = false
                        }
                        .padding()
                    }
                    .presentationDetents([.height(200)])
                }
                InputRow(title: "Height") {
                    TextField("cm", text: $height)
                        .multilineTextAlignment(.trailing)
                }
                InputRow(title: "Weight") {
                    TextField("kg", text: $weight)
                        .multilineTextAlignment(.trailing)
                }
                InputRow(title: "Email") {
                    TextField("example@email.com", text: $email)
                        .multilineTextAlignment(.trailing)
                        .keyboardType(.emailAddress)
                }
                InputRow(title: "Phone Number") {
                    HStack {
                        Button(action: {
                            showCountryCodePicker.toggle()
                        }) {
                            Text(countryCode.rawValue)
                                .frame(width: 60)
                        }
                        Divider()
                        TextField("01012345678", text: $phoneNumber)
                            .keyboardType(.phonePad)
                            .multilineTextAlignment(.trailing)
                    }
                }
                .sheet(isPresented: $showCountryCodePicker) {
                    VStack {
                        Picker("", selection: $countryCode) {
                            ForEach(CountryCode.allCases) { code in
                                Text(code.rawValue).tag(code)
                            }
                        }
                        .labelsHidden()
                        .pickerStyle(WheelPickerStyle())
                        Button("Done") {
                            showCountryCodePicker = false
                        }
                        .padding()
                    }
                    .presentationDetents([.height(200)])
                }
            }
            .padding(.horizontal)
            
            HStack(spacing: 16) {
                Button(action: {
                    resetFields()
                }) {
                    Text("Reset")
                        .font(.title)
                        .frame(maxWidth: .infinity, minHeight: 55)
                }
                .buttonStyle(.bordered)
                
                Button(action: {
                    handleNext()
                }) {
                    Text("Register")
                        .font(.title)
                        .frame(maxWidth: .infinity, minHeight: 55)
                }
                .buttonStyle(.borderedProminent)
                .disabled(email.isEmpty || phoneNumber.isEmpty)
            }
            .padding(.horizontal)
            
            Spacer(minLength: 20)
        }
    }
    
    private func resetFields() {
        birthDate = Date()
        gender = .male
        bloodType = .unknown
        height = ""
        weight = ""
        email = ""
        countryCode = .US
        phoneNumber = ""
    }
    
    private func handleNext() {
            let formatter = DateFormatter()
            formatter.dateFormat = "yyyy-MM-dd"
            let birthString = formatter.string(from: birthDate)
            
            let request = SignupRegisterRequest(
                name: name,
                email: email,
                gender: gender.rawValue.uppercased(),
                phoneNumber: phoneNumber,
                nationalCode: countryCode.rawValue,
                dateOfBirth: birthString,
                bloodType: bloodType.serverValue,
                height: Double(height) ?? 0.0,
                weight: Double(weight) ?? 0.0
            )
        Task {
            let success = await viewModel.patchRegistration(request: request)
            
            if success {
                appState.markSignupSuccess()
                appState.nextScreen = .disconnectedMain
            } else {
                feedback.triggerAlert("Registration failed. Please review the information you entered.")
            }
        }
    }
}

struct InputRow<Content: View>: View {
    let title: String
    let content: () -> Content

    var body: some View {
        HStack {
            Text(title)
            Spacer()
            content()
                .frame(width: 200)
        }
        .padding()
        .background(Color.gray.opacity(0.1))
        .cornerRadius(8)
    }
}

#Preview {
    SignupRegisterView()
}
