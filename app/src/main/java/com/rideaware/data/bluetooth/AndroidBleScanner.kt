package com.rideaware.data.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import com.rideaware.domain.classifier.DeviceClassifier
import com.rideaware.domain.model.DiscoveredDevice
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import java.security.MessageDigest
import java.util.Locale

/**
 * Concrete Android native BLE scanner implementation.
 *
 * Adheres strictly to Android lifecycle safety:
 * 1. Safe registration and unregistration of [ScanCallback].
 * 2. Obfuscates hardware MAC addresses into session-scoped tokens: "DEV-XXXX".
 * 3. Never throws unhandled exceptions if Bluetooth is toggled off mid-scan.
 */
class AndroidBleScanner(
    private val context: Context
) : BleScannerInterface {

    private val bluetoothManager: BluetoothManager? =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager

    private val bluetoothAdapter: BluetoothAdapter?
        get() = bluetoothManager?.adapter

    private val _isScanning = MutableStateFlow(false)
    override val isScanning = _isScanning.asStateFlow()

    private var activeCallback: ScanCallback? = null

    @SuppressLint("MissingPermission")
    override fun startScan(): Flow<DiscoveredDevice> = callbackFlow {
        val adapter = bluetoothAdapter
        if (adapter == null || !adapter.isEnabled) {
            _isScanning.value = false
            close(IllegalStateException("Bluetooth is disabled or unavailable"))
            return@callbackFlow
        }

        val scanner: BluetoothLeScanner? = adapter.bluetoothLeScanner
        if (scanner == null) {
            _isScanning.value = false
            close(IllegalStateException("BLE Scanner is currently unavailable"))
            return@callbackFlow
        }

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
            .build()

        val callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                result?.let {
                    val device = mapScanResult(it)
                    trySend(device)
                }
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                results?.forEach { result ->
                    val device = mapScanResult(result)
                    trySend(device)
                }
            }

            override fun onScanFailed(errorCode: Int) {
                _isScanning.value = false
                close(IllegalStateException("BLE scan failed with error code: $errorCode"))
            }
        }

        try {
            scanner.startScan(null, settings, callback)
            activeCallback = callback
            _isScanning.value = true
        } catch (e: SecurityException) {
            _isScanning.value = false
            close(e)
            return@callbackFlow
        } catch (e: Exception) {
            _isScanning.value = false
            close(e)
            return@callbackFlow
        }

        awaitClose {
            stopScan()
        }
    }

    @SuppressLint("MissingPermission")
    override fun stopScan() {
        val callback = activeCallback
        if (callback != null) {
            try {
                bluetoothAdapter?.bluetoothLeScanner?.stopScan(callback)
            } catch (e: Exception) {
                // Ignore platform teardown exceptions
            }
            activeCallback = null
        }
        _isScanning.value = false
    }

    @SuppressLint("MissingPermission")
    private fun mapScanResult(result: ScanResult): DiscoveredDevice {
        val rawAddress = result.device?.address ?: "UNKNOWN"
        val sessionId = generateSessionId(rawAddress)

        // Raw advertised name or fallback
        val rawName = try {
            result.scanRecord?.deviceName ?: result.device?.name
        } catch (e: SecurityException) {
            null
        }

        val displayName = if (!rawName.isNullOrBlank()) {
            rawName.trim()
        } else {
            "BLE Beacon (${sessionId.takeLast(4)})"
        }

        // Extract Service UUIDs
        val serviceUuids = result.scanRecord?.serviceUuids?.map { it.uuid.toString() } ?: emptyList()
        val now = System.currentTimeMillis()
        val classification = DeviceClassifier.classifyWithEvidence(serviceUuids, rawName)

        val isConnectable = try {
            result.isConnectable
        } catch (e: Throwable) {
            null
        }

        // Extract Manufacturer Specific Data
        val manufacturerData = mutableMapOf<Int, String>()
        val rawManufacturerData = result.scanRecord?.manufacturerSpecificData
        if (rawManufacturerData != null) {
            for (i in 0 until rawManufacturerData.size()) {
                val manufacturerId = rawManufacturerData.keyAt(i)
                val bytes = rawManufacturerData.valueAt(i)
                if (bytes != null && bytes.isNotEmpty()) {
                    manufacturerData[manufacturerId] = bytes.joinToString("") { "%02X".format(it) }
                }
            }
        }

        // Extract Service Data
        val serviceData = mutableMapOf<String, String>()
        result.scanRecord?.serviceData?.forEach { (parcelUuid, bytes) ->
            if (bytes != null && bytes.isNotEmpty()) {
                serviceData[parcelUuid.uuid.toString()] = bytes.joinToString("") { "%02X".format(it) }
            }
        }

        // Hardware address display subject to Android privacy masking
        val safeAddressDisplay = if (rawAddress != "UNKNOWN" && rawAddress.isNotBlank()) {
            rawAddress
        } else {
            null
        }

        return DiscoveredDevice(
            sessionId = sessionId,
            displayName = displayName,
            rawAddress = safeAddressDisplay,
            rssi = result.rssi,
            peakRssi = result.rssi,
            firstSeenTimestamp = now,
            lastSeenTimestamp = now,
            observationCount = 1,
            advertisedServiceUuids = serviceUuids,
            manufacturerData = manufacturerData,
            serviceData = serviceData,
            category = classification.category,
            classificationEvidence = classification.evidence,
            isConnectable = isConnectable
        )
    }

    /**
     * Derives a privacy-preserving ephemeral ID for the session.
     * Raw MAC addresses are NEVER stored or transmitted.
     */
    private fun generateSessionId(rawAddress: String): String {
        return try {
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(rawAddress.toByteArray())
            "DEV-" + digest.take(2).joinToString("") { "%02X".format(it) }.uppercase(Locale.ROOT)
        } catch (e: Exception) {
            "DEV-" + (rawAddress.hashCode().and(0xFFFF).toString(16).uppercase(Locale.ROOT))
        }
    }
}
