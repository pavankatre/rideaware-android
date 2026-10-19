# RideAware — Road Safety & Nearby BLE Device Explorer

RideAware is a modern Android application built with **Kotlin** and **Jetpack Compose (Material 3)**. It observes nearby Bluetooth Low Energy (BLE) advertisements, provides educational road-safety context, and demystifies the physics, platform constraints, and privacy boundaries of wireless device detection in traffic environments.

---

## 1. Features

- **Intuitive Safety Dashboard:** Quick overview of Bluetooth status, runtime permission states, and a prominent stationary scan trigger.
- **Privacy-Preserving BLE Scanning:** Observes discoverable BLE advertisements using battery-conscious scan modes (`SCAN_MODE_BALANCED`) without persisting hardware identifiers or transmitting data off-device.
- **Evidence-Based Device Categorization:** Neutral classification (`POSSIBLE_AUDIO_INDICATOR`, `OTHER_KNOWN_CATEGORY`, `UNKNOWN`) based strictly on published Bluetooth SIG Service UUIDs.
- **Realistic Signal Representation:** Shows RSSI in dBm alongside clear categorical indicators (`Good`, `Fair`, `Weak`), dispelling myths about exact distance calculation.
- **Session Scan Reports:** Local-only statistical summaries detailing scan duration, unique device volume, repeat sightings, and RSSI distribution.
- **Road Safety & Legal Guidance:** Dedicated educational guidelines reinforcing that riders must park safely before checking screens and must never confront other road users.
- **Modern Material 3 Design:** Refined, road-safety-inspired palette featuring deep navy, vibrant teal, and restrained warning amber with full Dark and Light theme support.

---

## 2. Technical Stack

- **OS Compatibility:** Android 8.0 (API level 26) through Android 15 (API level 35).
- **Language:** Kotlin 2.0.21
- **UI Framework:** Jetpack Compose (Compose BOM 2024.10.00)
- **Design System:** Material Design 3 (`androidx.compose.material3`)
- **Navigation:** Jetpack Navigation Compose (`2.8.3`)
- **Concurrency:** Kotlin Coroutines (`1.9.0`) & Kotlin Flow
- **Build System:** Gradle 8.10 with Kotlin DSL (`build.gradle.kts`)

---

## 3. Project Documentation

This repository contains detailed technical documentation:

1. [PRD.md](file:///d:/New%20folder/app/PRD.md) — Product requirements, user personas, functional specs, and acceptance criteria.
2. [Architecture.md](file:///d:/New%20folder/app/Architecture.md) — MVVM design, unidirectional data flow, package layout, and thread boundaries.
3. [Rules.md](file:///d:/New%20folder/app/Rules.md) — Architectural rules, coding standards, and data ethics guidelines.
4. [Design.md](file:///d:/New%20folder/app/Design.md) — Color tokens, typography, component blueprints, and accessibility standards.
5. [Task.md](file:///d:/New%20folder/app/Task.md) — Phased implementation checklist and status tracker.
6. [Memory.md](file:///d:/New%20folder/app/Memory.md) — Architectural log and technical decision history.

---

## 4. How to Build & Run

### Prerequisites
- **Android Studio Ladybug (2024.2.1+)** or higher
- **JDK 17 or JDK 21**
- Physical Android phone or emulator running Android 8.0+ (API 26+) with Bluetooth Low Energy support.

### Building via Android Studio
1. Launch Android Studio.
2. Select **Open** and choose the `RideAware/` root directory (`d:/New folder/app`).
3. Allow Gradle to synchronize project dependencies.
4. Select your target device and click **Run (Shift + F10)**.

### Building via Command Line
```powershell
# In PowerShell / Command Prompt inside the project directory:
.\gradlew.bat assembleDebug

# Execute automated unit tests:
.\gradlew.bat testDebugUnitTest
```

---

## 5. Bluetooth & Privacy Guarantees

- **No Remote Tracking:** All data remains local in device RAM and is discarded on session reset or app closure.
- **Obfuscated Identifiers:** Hardware MAC addresses are pseudonymized into session tokens (`DEV-XXXX`).
- **Zero Accusations:** RideAware never accuses any road user of distracted driving or unsafe behavior based on radio signals.

