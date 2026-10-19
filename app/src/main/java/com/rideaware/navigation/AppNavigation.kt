package com.rideaware.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rideaware.ui.screens.HomeScreen
import com.rideaware.ui.screens.NearbyDevicesScreen
import com.rideaware.ui.screens.SafetyScreen
import com.rideaware.ui.screens.ScanReportScreen
import com.rideaware.ui.screens.SettingsScreen
import com.rideaware.ui.viewmodel.ScanUiState
import com.rideaware.ui.viewmodel.ScanViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Nearby : Screen("nearby", "Nearby", Icons.Default.BluetoothSearching)
    object Report : Screen("report", "Report", Icons.Default.Assessment)
    object Safety : Screen("safety", "Safety", Icons.Default.Security)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

@Composable
fun AppNavigation(
    viewModel: ScanViewModel,
    uiState: ScanUiState,
    onRequestPermissions: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val navigationItems = listOf(
        Screen.Home,
        Screen.Nearby,
        Screen.Report,
        Screen.Safety,
        Screen.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                navigationItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = (screen.route != Screen.Home.route)
                                    }
                                    launchSingleTop = true
                                    restoreState = (screen.route != Screen.Home.route)
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    uiState = uiState,
                    onStartScan = { viewModel.startScan() },
                    onStopScan = { viewModel.stopScan() },
                    onNavigateToNearby = { navController.navigate(Screen.Nearby.route) },
                    onRequestPermissions = onRequestPermissions
                )
            }

            composable(Screen.Nearby.route) {
                NearbyDevicesScreen(
                    uiState = uiState,
                    onSearchChange = { viewModel.setSearchQuery(it) },
                    onSortChange = { viewModel.setSortOrder(it) },
                    onToggleAudioFilter = { viewModel.toggleAudioFilter() }
                )
            }

            composable(Screen.Report.route) {
                ScanReportScreen(
                    report = uiState.scanReport,
                    onClearSession = { viewModel.clearSession() }
                )
            }

            composable(Screen.Safety.route) {
                SafetyScreen()
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    selectedDurationSeconds = uiState.selectedDurationSeconds,
                    onDurationChange = { viewModel.setScanDuration(it) }
                )
            }
        }
    }
}
