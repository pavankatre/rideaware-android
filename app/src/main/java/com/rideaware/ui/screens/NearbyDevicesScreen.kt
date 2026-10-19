package com.rideaware.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rideaware.domain.model.DiscoveredDevice
import com.rideaware.ui.components.DeviceDetailsDialog
import com.rideaware.ui.components.ObservationCard
import com.rideaware.ui.viewmodel.DeviceSortOrder
import com.rideaware.ui.viewmodel.ScanUiState

@Composable
fun NearbyDevicesScreen(
    uiState: ScanUiState,
    onSearchChange: (String) -> Unit,
    onSortChange: (DeviceSortOrder) -> Unit,
    onToggleAudioFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDeviceForDetails by remember { mutableStateOf<DiscoveredDevice?>(null) }

    // Display inspection dialog when a device is selected
    selectedDeviceForDetails?.let { device ->
        DeviceDetailsDialog(
            device = device,
            onDismiss = { selectedDeviceForDetails = null }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = onSearchChange,
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
            },
            placeholder = { Text("Filter by name or session ID...") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter & Sort Row
        androidx.compose.foundation.lazy.LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                FilterChip(
                    selected = uiState.filterAudioOnly,
                    onClick = onToggleAudioFilter,
                    label = { Text("Audio Indicators") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }

            item {
                FilterChip(
                    selected = uiState.sortOrder == DeviceSortOrder.SIGNAL_STRENGTH_DESC,
                    onClick = { onSortChange(DeviceSortOrder.SIGNAL_STRENGTH_DESC) },
                    label = { Text("Strongest RSSI") }
                )
            }

            item {
                FilterChip(
                    selected = uiState.sortOrder == DeviceSortOrder.OBSERVATION_COUNT_DESC,
                    onClick = { onSortChange(DeviceSortOrder.OBSERVATION_COUNT_DESC) },
                    label = { Text("Most Active") }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Honest Device List or Empty State
        if (uiState.devices.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "No Devices",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (uiState.isScanning) "Listening for nearby broadcasts..." else "No Devices Observed Yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "This does not mean the area is free of electronics. Many devices (including connected earphones) do not broadcast BLE packets.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text(
                        text = "${uiState.devices.size} Discovered Devices",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                items(
                    items = uiState.devices,
                    key = { it.sessionId }
                ) { device ->
                    ObservationCard(
                        device = device,
                        onClick = { selectedDeviceForDetails = device }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
