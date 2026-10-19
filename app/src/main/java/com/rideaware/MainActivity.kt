package com.rideaware

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rideaware.navigation.AppNavigation
import com.rideaware.permissions.PermissionHelper
import com.rideaware.ui.theme.RideAwareTheme
import com.rideaware.ui.viewmodel.BluetoothStatus
import com.rideaware.ui.viewmodel.ScanViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ScanViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = application as RideAwareApplication
                return ScanViewModel(app.scanRepository) as T
            }
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        checkBluetoothAndPermissions()
    }

    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                checkBluetoothAndPermissions()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerReceiver(
            bluetoothReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        )

        setContent {
            RideAwareTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                    AppNavigation(
                        viewModel = viewModel,
                        uiState = uiState,
                        onRequestPermissions = {
                            permissionLauncher.launch(PermissionHelper.getRequiredPermissions())
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkBluetoothAndPermissions()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(bluetoothReceiver)
        } catch (e: Exception) {
            // Receiver might not be registered
        }
    }

    private fun checkBluetoothAndPermissions() {
        val hasBleFeature = packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
        if (!hasBleFeature) {
            viewModel.setBluetoothStatus(BluetoothStatus.UNSUPPORTED)
            return
        }

        if (!PermissionHelper.hasAllPermissions(this)) {
            viewModel.setBluetoothStatus(BluetoothStatus.PERMISSION_REQUIRED)
            return
        }

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        val adapter = bluetoothManager?.adapter

        if (adapter == null) {
            viewModel.setBluetoothStatus(BluetoothStatus.UNSUPPORTED)
        } else if (!adapter.isEnabled) {
            viewModel.setBluetoothStatus(BluetoothStatus.DISABLED)
        } else {
            viewModel.setBluetoothStatus(BluetoothStatus.READY)
        }
    }
}
