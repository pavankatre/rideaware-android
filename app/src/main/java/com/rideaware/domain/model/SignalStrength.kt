package com.rideaware.domain.model

/**
 * Signal strength classifications with realistic engineering boundaries.
 * RSSI is strongly affected by barriers, human body absorption, and vehicle materials.
 */
enum class SignalStrength(val label: String, val minRssi: Int, val maxRssi: Int) {
    GOOD(label = "Strong Signal", minRssi = -65, maxRssi = 0),
    FAIR(label = "Moderate Signal", minRssi = -85, maxRssi = -66),
    WEAK(label = "Weak / Distant Signal", minRssi = -120, maxRssi = -86);

    companion object {
        fun fromRssi(rssi: Int): SignalStrength {
            return when {
                rssi >= -65 -> GOOD
                rssi >= -85 -> FAIR
                else -> WEAK
            }
        }
    }
}
