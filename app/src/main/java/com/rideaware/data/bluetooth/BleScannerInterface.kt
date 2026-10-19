package com.rideaware.data.bluetooth

import com.rideaware.domain.model.DiscoveredDevice
import kotlinx.coroutines.flow.Flow

interface BleScannerInterface {
    val isScanning: Flow<Boolean>
    fun startScan(): Flow<DiscoveredDevice>
    fun stopScan()
}
