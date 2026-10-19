package com.rideaware.domain.model

/**
 * Aggregated metrics computed honestly from real session observations.
 */
data class ScanReportData(
    val startTime: Long = 0L,
    val endTime: Long = 0L,
    val durationSeconds: Long = 0L,
    val totalUniqueDevices: Int = 0,
    val totalObservations: Int = 0,
    val audioIndicatorsCount: Int = 0,
    val goodSignalCount: Int = 0,
    val fairSignalCount: Int = 0,
    val weakSignalCount: Int = 0,
    val averageRssi: Int? = null,
    val maxRssi: Int? = null,
    val minRssi: Int? = null
)
