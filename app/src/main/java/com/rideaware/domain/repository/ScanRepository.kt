package com.rideaware.domain.repository

import com.rideaware.domain.model.DiscoveredDevice
import com.rideaware.domain.model.ScanReportData
import kotlinx.coroutines.flow.StateFlow

interface ScanRepository {
    /** Observable stream of deduplicated devices observed during the current session */
    val devices: StateFlow<List<DiscoveredDevice>>

    /** Observable stream of current scan status */
    val isScanning: StateFlow<Boolean>

    /** Current accumulated session statistics */
    val sessionReport: StateFlow<ScanReportData>

    /** Total number of BLE advertisement packets received in this session */
    val totalObservationsCount: StateFlow<Int>

    /** Starts active scanning with specified timeout (in seconds). 0 = manual stop */
    suspend fun startScan(durationSeconds: Long)

    /** Stops active BLE scanning */
    suspend fun stopScan()

    /** Resets all accumulated session data */
    fun clearSession()
}
