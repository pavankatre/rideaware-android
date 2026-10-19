package com.rideaware.ui.viewmodel

import com.rideaware.domain.model.DiscoveredDevice
import com.rideaware.domain.model.ScanReportData

enum class BluetoothStatus {
    UNKNOWN,
    READY,
    DISABLED,
    UNSUPPORTED,
    PERMISSION_REQUIRED
}

enum class DeviceSortOrder {
    SIGNAL_STRENGTH_DESC,
    OBSERVATION_COUNT_DESC,
    LAST_SEEN_DESC
}

data class ScanUiState(
    val bluetoothStatus: BluetoothStatus = BluetoothStatus.READY,
    val isScanning: Boolean = false,
    val remainingSeconds: Int = 30,
    val selectedDurationSeconds: Long = 30L,
    val devices: List<DiscoveredDevice> = emptyList(),
    val totalObservationsCount: Int = 0,
    val scanReport: ScanReportData = ScanReportData(),
    val searchQuery: String = "",
    val sortOrder: DeviceSortOrder = DeviceSortOrder.SIGNAL_STRENGTH_DESC,
    val filterAudioOnly: Boolean = false,
    val errorMessage: String? = null
)
