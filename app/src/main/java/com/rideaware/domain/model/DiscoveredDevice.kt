package com.rideaware.domain.model

/**
 * Domain representation of an observed BLE beacon advertisement.
 *
 * Privacy Notice:
 * [sessionId] is an ephemeral, obfuscated session identifier (e.g. "DEV-XXXX")
 * derived at ingestion. True hardware MAC addresses are never exposed to the UI or stored.
 */
data class DiscoveredDevice(
    val sessionId: String,
    val displayName: String,
    val rawAddress: String? = null,
    val rssi: Int,
    val peakRssi: Int = rssi,
    val signalStrength: SignalStrength = SignalStrength.fromRssi(rssi),
    val firstSeenTimestamp: Long,
    val lastSeenTimestamp: Long,
    val observationCount: Int = 1,
    val advertisedServiceUuids: List<String> = emptyList(),
    val manufacturerData: Map<Int, String> = emptyMap(),
    val serviceData: Map<String, String> = emptyMap(),
    val category: DeviceCategory = DeviceCategory.UNKNOWN,
    val classificationEvidence: String? = null,
    val isConnectable: Boolean? = null
)
