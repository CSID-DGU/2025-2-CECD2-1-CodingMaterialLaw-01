package com.example.iot_air_quality_android.data.model.response

data class ProjectInfoResponse(
    val pmEmail: String,
    val projectTitle: String,
    val participant: Int,
    val description: String,
    val projectType: String,
    val startDate: String,
    val endDate: String,
    val createdAt: String,

    // 개인정보
    val email: Boolean,
    val gender: Boolean,
    val phoneNumber: Boolean,
    val dateOfBirth: Boolean,
    val bloodType: Boolean,
    val height: Boolean,
    val weight: Boolean,
    val name: Boolean,

    // 헬스 데이터
    val stepCount: Boolean,
    val runningSpeed: Boolean,
    val basalEnergyBurned: Boolean,
    val activeEnergyBurned: Boolean,
    val sleepAnalysis: Boolean,
    val heartRate: Boolean,
    val oxygenSaturation: Boolean,
    val bloodPressureSystolic: Boolean,
    val bloodPressureDiastolic: Boolean,
    val respiratoryRate: Boolean,
    val bodyTemperature: Boolean,
    val ecgData: Boolean,
    val watchDeviceLatitude: Boolean,
    val watchDeviceLongitude: Boolean,

    // 공기질 데이터
    val pm25Value: Boolean,
    val pm25Level: Boolean,
    val pm10Value: Boolean,
    val pm10Level: Boolean,
    val temperature: Boolean,
    val temperatureLevel: Boolean,
    val humidity: Boolean,
    val humidityLevel: Boolean,
    val co2Value: Boolean,
    val co2Level: Boolean,
    val vocValue: Boolean,
    val vocLevel: Boolean,
    val picoDeviceLatitude: Boolean,
    val picoDeviceLongitude: Boolean
)

/** iOS의 BoolField 대응용 */
data class ProjectBoolField(
    val label: String,
    val value: Boolean
)

/** iOS extension처럼 카테고리별로 묶기 */
fun ProjectInfoResponse.personalFields(): List<ProjectBoolField> =
    listOf(
        ProjectBoolField("Email", email),
        ProjectBoolField("Gender", gender),
        ProjectBoolField("Phone Number", phoneNumber),
        ProjectBoolField("Date of Birth", dateOfBirth),
        ProjectBoolField("Blood Type", bloodType),
        ProjectBoolField("Height", height),
        ProjectBoolField("Weight", weight),
        ProjectBoolField("Name", name)
    )

fun ProjectInfoResponse.healthFields(): List<ProjectBoolField> =
    listOf(
        ProjectBoolField("Step Count", stepCount),
        ProjectBoolField("Running Speed", runningSpeed),
        ProjectBoolField("Basal Energy Burned", basalEnergyBurned),
        ProjectBoolField("Active Energy Burned", activeEnergyBurned),
        ProjectBoolField("Sleep Analysis", sleepAnalysis),
        ProjectBoolField("Heart Rate", heartRate),
        ProjectBoolField("Oxygen Saturation", oxygenSaturation),
        ProjectBoolField("Systolic BP", bloodPressureSystolic),
        ProjectBoolField("Diastolic BP", bloodPressureDiastolic),
        ProjectBoolField("Respiratory Rate", respiratoryRate),
        ProjectBoolField("Body Temperature", bodyTemperature),
        ProjectBoolField("ECG Data", ecgData),
        ProjectBoolField("Watch Latitude", watchDeviceLatitude),
        ProjectBoolField("Watch Longitude", watchDeviceLongitude)
    )

fun ProjectInfoResponse.airFields(): List<ProjectBoolField> =
    listOf(
        ProjectBoolField("PM2.5 Value", pm25Value),
        ProjectBoolField("PM2.5 Level", pm25Level),
        ProjectBoolField("PM10 Value", pm10Value),
        ProjectBoolField("PM10 Level", pm10Level),
        ProjectBoolField("Temperature", temperature),
        ProjectBoolField("Temperature Level", temperatureLevel),
        ProjectBoolField("Humidity", humidity),
        ProjectBoolField("Humidity Level", humidityLevel),
        ProjectBoolField("CO2 Value", co2Value),
        ProjectBoolField("CO2 Level", co2Level),
        ProjectBoolField("VOC Value", vocValue),
        ProjectBoolField("VOC Level", vocLevel),
        ProjectBoolField("Pico Latitude", picoDeviceLatitude),
        ProjectBoolField("Pico Longitude", picoDeviceLongitude)
    )
