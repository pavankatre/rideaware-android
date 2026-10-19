package com.rideaware.domain.classifier

import com.rideaware.domain.model.DeviceCategory
import java.util.Locale

/**
 * Objective, evidence-based classifier for BLE advertisements.
 *
 * Ground Rules:
 * 1. Only classify as POSSIBLE_AUDIO_INDICATOR if standard Bluetooth SIG Audio Service UUIDs
 *    or clear audio naming evidence is present.
 * 2. Never assume that a generic BLE advertisement belongs to an earphone or distracted driver.
 * 3. Default to UNKNOWN when evidence is absent or ambiguous.
 */
object DeviceClassifier {

    // Standard 16-bit UUID representations in 128-bit Bluetooth Base UUID space
    // 0000XXXX-0000-1000-8000-00805F9B34FB
    private val AUDIO_SERVICE_UUID_PATTERNS = listOf(
        "0000110a", // A2DP Audio Source
        "0000110b", // A2DP Audio Sink (Headphones / Earphones / Car receivers)
        "0000110c", // A/V Remote Control Target
        "0000110e", // A/V Remote Control
        "0000111e", // Handsfree (HFP)
        "00001108", // Headset Profile (HSP)
        "0000184e", // Audio Stream Control Service (LE Audio)
        "0000184f", // Broadcast Audio Scan Service (LE Audio)
        "00001850", // Published Audio Capabilities (LE Audio)
        "00001851", // Basic Audio Announcement Service (LE Audio)
        "00001852"  // Broadcast Audio Announcement Service (LE Audio)
    )

    private val KNOWN_OTHER_SERVICE_UUID_PATTERNS = listOf(
        "0000180d", // Heart Rate
        "0000180f", // Battery Service
        "00001812", // Human Interface Device (Keyboard/Mouse)
        "00001816", // Cycling Speed and Cadence
        "00001818", // Cycling Power
        "00001802", // Immediate Alert
        "00001803"  // Link Loss
    )

    private val AUDIO_NAME_KEYWORDS = listOf(
        "buds", "earphone", "headphone", "headset", "airpods",
        "soundcore", "galaxy buds", "earbuds", "audio", "speaker",
        "wh-1000", "wf-1000", "freebuds", "tune", "airdots"
    )

    data class ClassificationResult(
        val category: DeviceCategory,
        val evidence: String
    )

    fun classify(serviceUuids: List<String>, rawDeviceName: String?): DeviceCategory {
        return classifyWithEvidence(serviceUuids, rawDeviceName).category
    }

    fun classifyWithEvidence(serviceUuids: List<String>, rawDeviceName: String?): ClassificationResult {
        val cleanUuids = serviceUuids.map { it.lowercase(Locale.ROOT) }

        // 1. Check for standard Bluetooth SIG Audio UUIDs
        val matchedAudioUuid = cleanUuids.firstOrNull { uuid ->
            AUDIO_SERVICE_UUID_PATTERNS.any { pattern -> uuid.contains(pattern) }
        }
        if (matchedAudioUuid != null) {
            return ClassificationResult(
                category = DeviceCategory.POSSIBLE_AUDIO_INDICATOR,
                evidence = "Advertised standard Bluetooth SIG Audio Service UUID ($matchedAudioUuid)"
            )
        }

        // 2. Check for explicit audio keywords in broadcast name if available
        if (!rawDeviceName.isNullOrBlank()) {
            val lowerName = rawDeviceName.lowercase(Locale.ROOT)
            val matchedKeyword = AUDIO_NAME_KEYWORDS.firstOrNull { keyword -> lowerName.contains(keyword) }
            if (matchedKeyword != null) {
                return ClassificationResult(
                    category = DeviceCategory.POSSIBLE_AUDIO_INDICATOR,
                    evidence = "Broadcast device name matched audio indicator keyword: '$matchedKeyword'"
                )
            }
        }

        // 3. Check for other recognized peripherals
        val matchedOtherUuid = cleanUuids.firstOrNull { uuid ->
            KNOWN_OTHER_SERVICE_UUID_PATTERNS.any { pattern -> uuid.contains(pattern) }
        }
        if (matchedOtherUuid != null) {
            return ClassificationResult(
                category = DeviceCategory.OTHER_KNOWN_CATEGORY,
                evidence = "Advertised peripheral service UUID ($matchedOtherUuid)"
            )
        }

        return ClassificationResult(
            category = DeviceCategory.UNKNOWN,
            evidence = "No known audio or specific peripheral UUIDs/keywords detected in broadcast payload"
        )
    }
}
