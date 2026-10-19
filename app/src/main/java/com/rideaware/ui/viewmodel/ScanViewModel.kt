package com.rideaware.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rideaware.domain.model.DeviceCategory
import com.rideaware.domain.model.DiscoveredDevice
import com.rideaware.domain.repository.ScanRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScanViewModel(
    private val repository: ScanRepository
) : ViewModel() {

    private val _internalState = MutableStateFlow(ScanUiState())
    private var countdownJob: Job? = null

    val uiState: StateFlow<ScanUiState> = combine(
        _internalState,
        repository.devices,
        repository.isScanning,
        repository.totalObservationsCount,
        repository.sessionReport
    ) { state, devices, isScanning, totalObs, report ->
        val filtered = devices
            .filter { device ->
                if (state.filterAudioOnly) {
                    device.category == DeviceCategory.POSSIBLE_AUDIO_INDICATOR
                } else true
            }
            .filter { device ->
                if (state.searchQuery.isNotBlank()) {
                    device.displayName.contains(state.searchQuery, ignoreCase = true) ||
                            device.sessionId.contains(state.searchQuery, ignoreCase = true)
                } else true
            }
            .sortedWith { a, b ->
                when (state.sortOrder) {
                    DeviceSortOrder.SIGNAL_STRENGTH_DESC -> b.rssi.compareTo(a.rssi)
                    DeviceSortOrder.OBSERVATION_COUNT_DESC -> b.observationCount.compareTo(a.observationCount)
                    DeviceSortOrder.LAST_SEEN_DESC -> b.lastSeenTimestamp.compareTo(a.lastSeenTimestamp)
                }
            }

        state.copy(
            devices = filtered,
            isScanning = isScanning,
            totalObservationsCount = totalObs,
            scanReport = report
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ScanUiState()
    )

    fun startScan() {
        val duration = _internalState.value.selectedDurationSeconds
        viewModelScope.launch {
            try {
                repository.startScan(duration)
                startCountdown(duration.toInt())
            } catch (e: Exception) {
                _internalState.value = _internalState.value.copy(
                    errorMessage = e.localizedMessage ?: "Failed to initiate BLE scan"
                )
            }
        }
    }

    fun stopScan() {
        countdownJob?.cancel()
        countdownJob = null
        viewModelScope.launch {
            repository.stopScan()
        }
    }

    fun clearSession() {
        repository.clearSession()
    }

    fun setBluetoothStatus(status: BluetoothStatus) {
        _internalState.value = _internalState.value.copy(bluetoothStatus = status)
    }

    fun setScanDuration(seconds: Long) {
        _internalState.value = _internalState.value.copy(
            selectedDurationSeconds = seconds,
            remainingSeconds = seconds.toInt()
        )
    }

    fun setSearchQuery(query: String) {
        _internalState.value = _internalState.value.copy(searchQuery = query)
    }

    fun setSortOrder(order: DeviceSortOrder) {
        _internalState.value = _internalState.value.copy(sortOrder = order)
    }

    fun toggleAudioFilter() {
        _internalState.value = _internalState.value.copy(
            filterAudioOnly = !_internalState.value.filterAudioOnly
        )
    }

    fun clearError() {
        _internalState.value = _internalState.value.copy(errorMessage = null)
    }

    private fun startCountdown(seconds: Int) {
        countdownJob?.cancel()
        if (seconds <= 0) return

        countdownJob = viewModelScope.launch {
            var left = seconds
            _internalState.value = _internalState.value.copy(remainingSeconds = left)
            while (left > 0) {
                delay(1000)
                left -= 1
                _internalState.value = _internalState.value.copy(remainingSeconds = left)
            }
        }
    }
}
