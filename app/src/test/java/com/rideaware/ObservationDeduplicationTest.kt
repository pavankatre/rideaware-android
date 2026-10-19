package com.rideaware

import com.rideaware.data.bluetooth.BleScannerInterface
import com.rideaware.data.repository.ScanRepositoryImpl
import com.rideaware.domain.model.DeviceCategory
import com.rideaware.domain.model.DiscoveredDevice
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ObservationDeduplicationTest {

    private class FakeBleScanner : BleScannerInterface {
        val emitFlow = MutableSharedFlow<DiscoveredDevice>()
        override val isScanning = MutableSharedFlow<Boolean>()

        override fun startScan(): Flow<DiscoveredDevice> = emitFlow.asSharedFlow()
        override fun stopScan() {}
    }

    @Test
    fun repeatedSightingsOfSameDevice_incrementsCountAndKeepsSingleEntry() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val testScope = TestScope(testDispatcher)

        val fakeScanner = FakeBleScanner()
        val repository = ScanRepositoryImpl(fakeScanner, testScope)

        repository.startScan(0)

        val device1 = DiscoveredDevice(
            sessionId = "DEV-1001",
            displayName = "BLE Beacon (1001)",
            rssi = -75,
            firstSeenTimestamp = 1000L,
            lastSeenTimestamp = 1000L,
            observationCount = 1
        )

        val device1SecondPacket = DiscoveredDevice(
            sessionId = "DEV-1001",
            displayName = "Sony WH-1000XM4", // Named beacon arrived later
            rssi = -70,
            firstSeenTimestamp = 1000L,
            lastSeenTimestamp = 1500L,
            observationCount = 1,
            category = DeviceCategory.POSSIBLE_AUDIO_INDICATOR
        )

        val device2 = DiscoveredDevice(
            sessionId = "DEV-2002",
            displayName = "Smart Watch",
            rssi = -80,
            firstSeenTimestamp = 1200L,
            lastSeenTimestamp = 1200L,
            observationCount = 1
        )

        testScope.launch {
            fakeScanner.emitFlow.emit(device1)
            fakeScanner.emitFlow.emit(device1SecondPacket)
            fakeScanner.emitFlow.emit(device2)
        }

        testScheduler.advanceUntilIdle()

        val currentDevices = repository.devices.value
        assertEquals(2, currentDevices.size)

        val dev1 = currentDevices.find { it.sessionId == "DEV-1001" }
        assertEquals(2, dev1?.observationCount)
        assertEquals(-70, dev1?.rssi)
        assertEquals(-70, dev1?.peakRssi)
        assertEquals("Sony WH-1000XM4", dev1?.displayName)
        assertEquals(DeviceCategory.POSSIBLE_AUDIO_INDICATOR, dev1?.category)

        assertEquals(3, repository.totalObservationsCount.value)
    }
}
