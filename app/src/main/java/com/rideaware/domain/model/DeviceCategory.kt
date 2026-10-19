package com.rideaware.domain.model

/**
 * Categorization of observed Bluetooth signals based strictly on verifiable technical evidence.
 */
enum class DeviceCategory(val label: String, val description: String) {
    POSSIBLE_AUDIO_INDICATOR(
        label = "Possible Audio Indicator",
        description = "Broadcasts known Bluetooth Audio/A2DP/LE-Audio service UUIDs or audio-related markers. Not proof of active audio playback or earphone usage."
    ),
    OTHER_KNOWN_CATEGORY(
        label = "Recognized Peripheral",
        description = "Broadcasts standard non-audio BLE profile markers (e.g. Heart Rate, Fitness, Proximity, HID)."
    ),
    UNKNOWN(
        label = "Unclassified BLE Device",
        description = "No recognizable audio or specific profile service UUID was detected in the broadcast."
    )
}
