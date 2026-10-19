package com.rideaware.data.repository

import com.rideaware.data.bluetooth.BleScannerInterface
import com.rideaware.domain.model.DeviceCategory
import com.rideaware.domain.model.DiscoveredDevice
import com.rideaware.domain.model.ScanReportData
import com.rideaware.domain.model.SignalStrength
import com.rideaware.domain.repository.ScanRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ScanRepositoryImpl(
    private val bleScanner: BleScannerInterface,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : ScanRepository {

    private val mutex = Mutex()
    private val deviceMap = mutableMapOf<String, DiscoveredDevice>()

    private val _devices = MutableStateFlow<List<DiscoveredDevice>>(emptyList())
    override val devices: StateFlow<List<DiscoveredDevice>> = _devices.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    override val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _sessionReport = MutableStateFlow(ScanReportData())
    override val sessionReport: StateFlow<ScanReportData> = _sessionReport.asStateFlow()

    private val _totalObservationsCount = MutableStateFlow(0)
    override val totalObservationsCount: StateFlow<Int> = _totalObservationsCount.asStateFlow()

    private var scanJob: Job? = null
    private var timerJob: Job? = null
    private var sessionStartTime: Long = 0L

    override suspend fun startScan(durationSeconds: Long) {
        if (_isScanning.value) return

        if (sessionStartTime == 0L) {
            sessionStartTime = System.currentTimeMillis()
        }

        _isScanning.value = true

        scanJob = scope.launch {
            bleScanner.startScan()
                .catch {
                    _isScanning.value = false
                }
                .collect { observedDevice ->
                    recordObservation(observedDevice)
                }
        }

        if (durationSeconds > 0) {
            timerJob = scope.launch {
                delay(durationSeconds * 1000)
                stopScan()
            }
        }
    }

    override suspend fun stopScan() {
        timerJob?.cancel()
        timerJob = null

        scanJob?.cancel()
        scanJob = null

        bleScanner.stopScan()
        _isScanning.value = false

        updateSessionReport(isSessionEnded = true)
    }

    override fun clearSession() {
        scope.launch {
            mutex.withLock {
                deviceMap.clear()
                _devices.value = emptyList()
                _totalObservationsCount.value = 0
                sessionStartTime = 0L
                _sessionReport.value = ScanReportData()
            }
        }
    }

    private suspend fun recordObservation(incoming: DiscoveredDevice) {
        mutex.withLock {
            _totalObservationsCount.value += 1

            val existing = deviceMap[incoming.sessionId]
            val updated = if (existing != null) {
                existing.copy(
                    // Update latest RSSI, peak RSSI, and observation count
                    rssi = incoming.rssi,
                    peakRssi = maxOf(existing.peakRssi, incoming.rssi),
                    signalStrength = SignalStrength.fromRssi(incoming.rssi),
                    lastSeenTimestamp = incoming.lastSeenTimestamp,
                    observationCount = existing.observationCount + 1,
                    // Preserve or enrich display name if better name appears
                    displayName = if (existing.displayName.startsWith("BLE Beacon") && !incoming.displayName.startsWith("BLE Beacon")) {
                        incoming.displayName
                    } else {
                        existing.displayName
                    },
                    rawAddress = existing.rawAddress ?: incoming.rawAddress,
                    category = if (existing.category == DeviceCategory.UNKNOWN && incoming.category != DeviceCategory.UNKNOWN) {
                        incoming.category
                    } else {
                        existing.category
                    },
                    classificationEvidence = incoming.classificationEvidence ?: existing.classificationEvidence,
                    advertisedServiceUuids = if (existing.advertisedServiceUuids.isEmpty()) incoming.advertisedServiceUuids else existing.advertisedServiceUuids,
                    manufacturerData = if (existing.manufacturerData.isEmpty()) incoming.manufacturerData else existing.manufacturerData,
                    serviceData = if (existing.serviceData.isEmpty()) incoming.serviceData else existing.serviceData
                )
            } else {
                incoming
            }

            deviceMap[incoming.sessionId] = updated
            _devices.value = deviceMap.values.toList()

            updateSessionReport(isSessionEnded = false)
        }
    }

    private fun updateSessionReport(isSessionEnded: Boolean) {
        val now = System.currentTimeMillis()
        val durationSec = if (sessionStartTime > 0) (now - sessionStartTime) / 1000 else 0L

        val currentDevices = deviceMap.values.toList()
        val totalUnique = currentDevices.size
        val audioCount = currentDevices.count { it.category == DeviceCategory.POSSIBLE_AUDIO_INDICATOR }
        val goodCount = currentDevices.count { it.signalStrength == SignalStrength.GOOD }
        val fairCount = currentDevices.count { it.signalStrength == SignalStrength.FAIR }
        val weakCount = currentDevices.count { it.signalStrength == SignalStrength.WEAK }

        val rssiList = currentDevices.map { it.rssi }
        val avgRssi = if (rssiList.isNotEmpty()) rssiList.average().toInt() else null
        val maxRssi = rssiList.maxOrNull()
        val minRssi = rssiList.minOrNull()

        _sessionReport.value = ScanReportData(
            startTime = sessionStartTime,
            endTime = if (isSessionEnded) now else 0L,
            durationSeconds = durationSec,
            totalUniqueDevices = totalUnique,
            totalObservations = _totalObservationsCount.value,
            audioIndicatorsCount = audioCount,
            goodSignalCount = goodCount,
            fairSignalCount = fairCount,
            weakSignalCount = weakCount,
            averageRssi = avgRssi,
            maxRssi = maxRssi,
            minRssi = minRssi
        )
    }
}
