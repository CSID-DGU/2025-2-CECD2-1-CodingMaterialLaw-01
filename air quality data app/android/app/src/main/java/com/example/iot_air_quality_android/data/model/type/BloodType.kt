package com.example.iot_air_quality_android.data.model.type

enum class BloodType(val display: String, val serverValue: String) {
    A_PLUS("A+", "A_PLUS"),
    A_MINUS("A-", "A_MINUS"),
    B_PLUS("B+", "B_PLUS"),
    B_MINUS("B-", "B_MINUS"),
    AB_PLUS("AB+", "AB_PLUS"),
    AB_MINUS("AB-", "AB_MINUS"),
    O_PLUS("O+", "O_PLUS"),
    O_MINUS("O-", "O_MINUS"),
    UNKNOWN("Unknown", "UNKNOWN");

    companion object {
        /** Spinner 표시용 문자열 리스트 */
        fun displayList(): List<String> = values().map { it.display }

        /** Spinner 선택값 → 서버 전송용 코드 변환 */
        fun fromDisplay(display: String): BloodType =
            values().find { it.display == display } ?: UNKNOWN
    }
}