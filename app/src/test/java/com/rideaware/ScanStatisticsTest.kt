package com.rideaware

import com.rideaware.data.bluetooth.BleScannerInterface
import com.rideaware.data.repository.ScanRepositoryImpl
import com.rideaware.domain.model.DeviceCategory
import com.rideaware.domain.model.DiscoveredDevice
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ScanStatisticsTest {

    private class FakeBleScanner : BleScannerInterface {
        val emitFlow = MutableSharedFlow<DiscoveredDevice>()
        override val isScanning = MutableSharedFlow<Boolean>()

        override fun startScan(): Flow<DiscoveredDevice> = emitFlow.asSharedFlow()
        override fun stopScan() {}
    }

    @Test
    fun sessionStatistics_accuratelyComputedFromObservations() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val testScope = TestScope(testDispatcher)

        val fakeScanner = FakeBleScanner()
        val repository = ScanRepositoryImpl(fakeScanner, testScope)

        repository.startScan(0)

        val devA = DiscoveredDevice(
            sessionId = "DEV-AAA1",
            displayName = "Audio Device",
            rssi = -60, // GOOD
            firstSeenTimestamp = 1000L,
            lastSeenTimestamp = 1000L,
            category = DeviceCategory.POSSIBLE_AUDIO_INDICATOR
        )

        val devB = DiscoveredDevice(
            sessionId = "DEV-BBB2",
            displayName = "BLE Tag",
            rssi = -75, // FAIR
            firstSeenTimestamp = 1000L,
            lastSeenTimestamp = 1000L,
            category = DeviceCategory.UNKNOWN
        )

        val devC = DiscoveredDevice(
            sessionId = "DEV-CCC3",
            displayName = "Faint Beacon",
            rssi = -90, // WEAK
            firstSeenTimestamp = 1000L,
            lastSeenTimestamp = 1000L,
            category = DeviceCategory.UNKNOWN
        )

        testScope.launch {
            fakeScanner.emitFlow.emit(devA)
            fakeScanner.emitFlow.emit(devB)
            fakeScanner.emitFlow.emit(devC)
        }

        testScheduler.advanceUntilIdle()

        val report = repository.sessionReport.value
        assertEquals(3, report.totalUniqueDevices)
        assertEquals(3, report.totalObservations)
        assertEquals(1, report.audioIndicatorsCount)
        assertEquals(1, report.goodSignalCount)
        assertEquals(1, report.fairSignalCount)
        assertEquals(1, report.weakSignalCount)
        assertEquals(-75, report.averageRssi) // (-60 + -75 + -90) / 3 = -75
        assertEquals(-60, report.maxRssi)
        assertEquals(-90, report.minRssi)
    }

    @Test
    fun clearSession_resetsAllStatisticsToZero() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val testScope = TestScope(testDispatcher)

        val fakeScanner = FakeBleScanner()
        val repository = ScanRepositoryImpl(fakeScanner, testScope)

        repository.startScan(0)

        testScope.launch {
            fakeScanner.emitFlow.emit(
                DiscoveredDevice(
                    sessionId = "DEV-AAA1",
                    displayName = "Test",
                    rssi = -50,
                    firstSeenTimestamp = 100L,
                    lastSeenTimestamp = 100L
                )
            )
        }
        testScheduler.advanceUntilIdle()

        assertEquals(1, repository.devices.value.size)

        repository.clearSession()
        testScheduler.advanceUntilIdle()

        assertEquals(0, repository.devices.value.size)
        assertEquals(0, repository.totalObservationsCount.value)
        assertEquals(0, repository.sessionReport.value.totalUniqueDevices)
    }
}
