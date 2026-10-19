package com.rideaware

import android.app.Application
import com.rideaware.data.bluetooth.AndroidBleScanner
import com.rideaware.data.repository.ScanRepositoryImpl
import com.rideaware.domain.repository.ScanRepository

class RideAwareApplication : Application() {

    // Centralized manual dependency container
    lateinit var scanRepository: ScanRepository
        private set

    override fun onCreate() {
        super.onCreate()

        val bleScanner = AndroidBleScanner(applicationContext)
        scanRepository = ScanRepositoryImpl(bleScanner)
    }
}
