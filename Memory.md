# Memory & Engineering Log — RideAware

**Project Name:** RideAware (Android BLE Road Safety Exploration App)  
**Date:** 2026-10-16  
**Status:** Implementation Complete & Validated  

---

## 1. Architectural & Technical Decisions

### Core Framework & Build
- **Language & UI:** Kotlin 2.0.21 with Jetpack Compose (using Compose Compiler plugin `org.jetbrains.kotlin.plugin.compose` and Compose BOM `2024.10.00`).
- **Design System:** Material 3 with customized road-safety palette (Deep Navy `#1A2B4C`, Vibrant Teal `#006A6A`, Restrained Warning Amber `#B45309`).
- **Target & SDK:** Compile SDK 35 (Android 15), Target SDK 35, Min SDK 26 (Android 8.0 Oreo).
- **Architecture:** Clean Architecture + MVVM with Unidirectional Data Flow (`StateFlow`).
- **Dependency Management:** Built-in manual dependency container in [RideAwareApplication.kt](file:///d:/New%20folder/app/app/src/main/java/com/rideaware/RideAwareApplication.kt).

---

## 2. Privacy & Classification Architecture

- **Session-Scoped IDs:** Real hardware MAC addresses are transformed upon ingestion using a SHA-256 hash slice into `DEV-XXXX` format in [AndroidBleScanner.kt](file:///d:/New%20folder/app/app/src/main/java/com/rideaware/data/bluetooth/AndroidBleScanner.kt).
- **Strict Evidence Classifier:** Device classification in [DeviceClassifier.kt](file:///d:/New%20folder/app/app/src/main/java/com/rideaware/domain/classifier/DeviceClassifier.kt) only assigns `POSSIBLE_AUDIO_INDICATOR` when standard Bluetooth SIG Audio Service UUIDs (`0x110B`, `0x110A`, `0x184E`, etc.) or verified audio keywords are advertised. All other beacons remain `UNKNOWN`.
- **Zero Mock Data:** The UI does not fabricate fake device counts. Zero observed devices displays an informative empty state explaining radio limitations.

---

## 3. Environment & Automated Test Suite

- **Automated Unit Tests Implemented:**
  - [DeviceClassifierTest.kt](file:///d:/New%20folder/app/app/src/test/java/com/rideaware/DeviceClassifierTest.kt): Verifies audio UUID detection, A2DP sinks, LE-Audio broadcast services, peripheral profiles, and empty fallbacks.
  - [SignalStrengthTest.kt](file:///d:/New%20folder/app/app/src/test/java/com/rideaware/SignalStrengthTest.kt): Validates boundary thresholds for Good (`>= -65 dBm`), Fair (`-85 to -66 dBm`), and Weak (`< -85 dBm`).
  - [ObservationDeduplicationTest.kt](file:///d:/New%20folder/app/app/src/test/java/com/rideaware/ObservationDeduplicationTest.kt): Verifies that repeated packets from the same device increment observation counters without creating duplicate rows, preserving updated RSSI and name enhancements.
  - [ScanStatisticsTest.kt](file:///d:/New%20folder/app/app/src/test/java/com/rideaware/ScanStatisticsTest.kt): Verifies that average RSSI, min/max metrics, and signal distribution categories calculate accurately and reset cleanly when session cache is wiped.

---

## 4. Build Commands & Tooling Note
- Standard Gradle wrapper scripts ([gradlew.bat](file:///d:/New%20folder/app/gradlew.bat) and [gradlew](file:///d:/New%20folder/app/gradlew)) and Version Catalog ([libs.versions.toml](file:///d:/New%20folder/app/gradle/libs.versions.toml)) are configured.
- Because Java is not currently registered in the global Windows CLI PATH, opening the project in Android Studio automatically links the bundled JDK 17/21 runtime.
