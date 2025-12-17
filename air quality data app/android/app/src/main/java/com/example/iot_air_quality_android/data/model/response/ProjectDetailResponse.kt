package com.example.iot_air_quality_android.data.model

data class ProjectDetail(
    val pmEmail: String,
    val projectTitle: String,
    val participant: Int,
    val description: String,
    val projectType: String,
    val startDate: String,
    val endDate: String,
    val createdAt: String,

    // Personal info
    val email: Boolean,
    val gender: Boolean,
    val phoneNumber: Boolean,
    val dateOfBirth: Boolean,
    val bloodType: Boolean,
    val height: Boolean,
    val weight: Boolean,
    val name: Boolean,

    // Health data
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

    // Air quality data
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
) {
    data class StringField(
        val label: String,
        val value: String
    )

    data class BoolField(
        val label: String,
        val value: Boolean
    )

    val metadataFields: List<StringField>
        get() = listOf(
            StringField("Title", projectTitle),
            StringField("Project Manager Email", pmEmail),
            StringField("Number of Participants", participant.toString()),
            StringField("Description", description),
            StringField("Project Type", projectType),
            StringField("Start Date", startDate),
            StringField("End Date", endDate),
            StringField("Created At", createdAt)
        )

    val personalFields: List<BoolField>
        get() = listOf(
            BoolField("Email", email),
            BoolField("Gender", gender),
            BoolField("Phone Number", phoneNumber),
            BoolField("Date of Birth", dateOfBirth),
            BoolField("Blood Type", bloodType),
            BoolField("Height", height),
            BoolField("Weight", weight),
            BoolField("Name", name)
        )

    val healthFields: List<BoolField>
        get() = listOf(
            BoolField("Step Count", stepCount),
            BoolField("Running Speed", runningSpeed),
            BoolField("Basal Energy Burned", basalEnergyBurned),
            BoolField("Active Energy Burned", activeEnergyBurned),
            BoolField("Sleep Analysis", sleepAnalysis),
            BoolField("Heart Rate", heartRate),
            BoolField("Oxygen Saturation", oxygenSaturation),
            BoolField("Systolic BP", bloodPressureSystolic),
            BoolField("Diastolic BP", bloodPressureDiastolic),
            BoolField("Respiratory Rate", respiratoryRate),
            BoolField("Body Temperature", bodyTemperature),
            BoolField("ECG Data", ecgData),
            BoolField("Watch Latitude", watchDeviceLatitude),
            BoolField("Watch Longitude", watchDeviceLongitude)
        )

    val airFields: List<BoolField>
        get() = listOf(
            BoolField("PM2.5 Value", pm25Value),
            BoolField("PM2.5 Level", pm25Level),
            BoolField("PM10 Value", pm10Value),
            BoolField("PM10 Level", pm10Level),
            BoolField("Temperature", temperature),
            BoolField("Temperature Level", temperatureLevel),
            BoolField("Humidity", humidity),
            BoolField("Humidity Level", humidityLevel),
            BoolField("CO2 Value", co2Value),
            BoolField("CO2 Level", co2Level),
            BoolField("VOC Value", vocValue),
            BoolField("VOC Level", vocLevel),
            BoolField("Pico Latitude", picoDeviceLatitude),
            BoolField("Pico Longitude", picoDeviceLongitude)
        )
}
