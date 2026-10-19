# Product Requirements Document (PRD) — RideAware

**Application Name:** RideAware  
**Platform:** Android (Min SDK 26, Target SDK 35, Compile SDK 35)  
**Language & Framework:** Kotlin 2.0.21, Jetpack Compose, Material Design 3  
**Architecture:** Clean Architecture + MVVM (Unidirectional Data Flow)  
**Status:** Approved for Implementation  
**Version:** 1.0.0-MVP  

---

## 1. Executive Summary & Vision

RideAware is a privacy-conscious, road-safety-oriented Android mobile application designed to observe and display nearby Bluetooth Low Energy (BLE) advertisements while providing clear educational context and safe usage reminders. 

The application addresses growing community curiosity regarding Bluetooth presence (such as wireless earphones, smart devices, and vehicle systems) in traffic environments, while explicitly debunking dangerous misconceptions around Bluetooth detection, exact distance calculation, and rider surveillance.

---

## 2. Problem Statement & Critical Technical Limitations

### 2.1 The Problem
Commuters and road safety advocates frequently express concern about road users being distracted by personal audio devices or mobile phones while operating two-wheelers. However, ordinary consumer smartphones rely on radio frequency scans (Bluetooth LE) which have strict physics and platform boundaries that are widely misunderstood.

### 2.2 Technical Realities & Anti-Misinformation Principles
A standard Android mobile application cannot:
1. Confirm whether a discovered Bluetooth device belongs to a motorcyclist, cyclist, pedestrian, or passenger in a car.
2. Determine whether a detected earphone or headphone is actively playing audio or actively worn in an ear.
3. Determine whether a nearby road user is distracted.
4. Guarantee discovery of nearby audio devices (many operate solely over classic Bluetooth BR/EDR, do not continuously broadcast BLE advertisements, or use non-resolvable random addresses).
5. Measure true physical distance using Received Signal Strength Indication (RSSI) alone (RSSI fluctuates wildly with human body blockage, weather, vehicle metals, and antenna orientation).
6. Guarantee a dependable 100–200 meter scanning radius (BLE range is typically 10–30m in real-world urban radio clutter).

**Guiding Rule:** RideAware NEVER labels any discovered device as "distracted rider" or "confirmed earphone in use". Neutral, scientifically precise terms like *"Nearby BLE Device"*, *"Possible Audio Device Indicator"*, and *"Insufficient Information"* are enforced across all UI screens.

---

## 3. Product Goals & Non-Goals

### 3.1 In-Scope Goals (MVP)
- **FR-001:** Full Android Studio / Gradle Kotlin DSL project supporting Android 8.0 (API 26) through Android 15 (API 35).
- **FR-002:** Android 12+ version-aware Bluetooth runtime permission handling (`BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT` with graceful fallback and permanent denial navigation).
- **FR-003:** Bluetooth adapter state monitoring and graceful UI recovery (Bluetooth Disabled / Unavailable / Ready).
- **FR-004:** Native BLE scanning with battery-conservative `ScanSettings.SCAN_MODE_BALANCED`, explicit start/stop controls, and scan session timer limits (configurable 15s / 30s / 60s).
- **FR-005:** Real-time discovery list showing session-scoped identifier, device name (or neutral fallback), RSSI in dBm with categorical signal level indicator (Weak / Fair / Good), timestamp, observation count, and advertisement service UUID indicators.
- **FR-006:** Conservative, evidence-based device classification (`POSSIBLE_AUDIO_INDICATOR`, `OTHER_KNOWN_CATEGORY`, `UNKNOWN`).
- **FR-007:** Device Details Inspection Dialog: Tapping any device card opens a rich inspection modal displaying real, un-fabricated BLE scan parameters: device name, Android-restricted address, current and peak RSSI (dBm), observation count, first/last detection timestamps, advertised Service UUIDs, manufacturer-specific data, service data payloads, and classification evidence with scientific RSSI caveats.
- **FR-008:** Session Scan Report detailing start/end timestamps, duration, unique device count, observation volume, RSSI distribution, and explicit limitation disclaimers.
- **FR-009:** Stationary-first Safety Screen educating riders to park safely before using the phone and warning against road confrontation.
- **FR-010:** Settings screen with Theme Selection, configurable Scan Duration Timeout, and Session Observation wiping (Clear Session Data).
- **FR-011:** Local-only, privacy-preserving zero-persistence architecture (no tracking, no cloud, no raw MAC address logging).

### 3.2 Non-Goals, Scope Decisions & Prohibitions
- **No Haptic Feedback / Vibration Alerts:** Not implemented in MVP. Bluetooth observations cannot reliably prove that a nearby individual is wearing earphones or is distracted; haptic alerts could trigger false alarms, road panic, or dangerous distractions.
- **Post-MVP Roadmap — Export / Share Report:** Planned for future releases to allow exporting or sharing high-level scan summary statistics (duration, device counts, scan errors) via Android Sharesheet without persistent device identifiers or sensitive raw radio logs.
- **No radio jamming:** (RF/Wi-Fi/GPS jamming is illegal and hazardous).
- **No interception, audio eavesdropping, or unauthorized Bluetooth pairing.**
- **No facial recognition, license plate recognition, or person tracking.**
- **No cloud database, user accounts, ad networks, or analytics SDKs.**
- **No background covert scanning or unsolicited foreground services without user initiation.**
- **No accusations or reporting to law enforcement.**

---

## 4. User Personas & Use Cases

### 4.1 Personas
- **Rider Alex (Safety-Conscious Commuter):** Wants to understand what signals surround him during commute stops and wants a tool that reminds him to remain focused.
- **Safety Educator Priya (Field Researcher):** Demonstrates to community groups why smartphone BLE cannot be used as an enforcement radar and teaches road vigilance.

### 4.2 Primary Use Cases
1. **UC-01 (Safe Exploration):** Alex parks his scooter, opens RideAware, starts a 30-second stationary scan, and observes nearby active BLE beacons with clear disclaimers.
2. **UC-02 (Session Review):** Alex inspects the Scan Report to see signal variations and total observations before clearing data.
3. **UC-03 (Educational Reference):** Priya displays the Safety and Limitations guides during a workshop without needing Bluetooth permissions.

---

## 5. Functional Requirements (FR)

| ID | Title | Description | Priority |
|---|---|---|---|
| **FR-001** | Modern UI & Theming | Material 3 Jetpack Compose UI with deep navy, teal, and restrained amber palette, supporting Light and Dark modes. | P0 |
| **FR-002** | Runtime Permissions | Transparent version-dependent request flow for Android 12+ (`BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT`) and pre-Android 12 (`ACCESS_FINE_LOCATION`). | P0 |
| **FR-003** | Adapter State Detection | Detect whether device has BLE capability and whether Bluetooth is turned on or off in real-time. | P0 |
| **FR-004** | Foreground BLE Scanner | Controlled scan session management with coroutine timeouts, preventing memory leaks and stale callbacks. | P0 |
| **FR-005** | Device Observation Feed | Real-time observation updates with deduplication by session ID, tracking packet counts and signal strength. | P0 |
| **FR-006** | Conservative Classifier | Audio classification strictly gated on known Bluetooth SIG Audio Service UUIDs (e.g. 0x110B, 0x110A, 0x180A) or standard audio device classes. | P1 |
| **FR-007** | Local Session Report | Statistical summary of session duration, device volume, and RSSI ranges without persistent storage. | P1 |
| **FR-008** | Dedicated Safety Portal | Always-accessible road safety instructions and platform disclaimer guidance. | P0 |
| **FR-009** | User Preference Controls | User-adjustable scan durations (15s, 30s, 60s, continuous manual) and instant session reset. | P1 |

---

## 6. Non-Functional Requirements (NFR)

- **NFR-001 (Performance):** Zero UI thread blocking. All BLE callbacks dispatch to `Dispatchers.Default` / `Dispatchers.IO` and emit via Kotlin `StateFlow`.
- **NFR-002 (Stability):** 0 uncaught exceptions when Bluetooth is toggled off during an active scan or when permissions are revoked.
- **NFR-003 (Privacy):** Hardware MAC addresses are transformed into session-specific obfuscated tokens (`DEV-XXXX`) for display and discarded on session end.
- **NFR-004 (Accessibility):** Touch targets ≥ 48dp, contrast ratio ≥ 4.5:1 for standard text, semantic content descriptions on all icons.
- **NFR-005 (Battery):** Scanning restricted to user-initiated foreground sessions with default 30-second auto-stop timer.

---

## 7. Definition of Done (DoD)

1. Android Gradle project configured with Kotlin 2.0+ and Jetpack Compose.
2. Architecture follows MVVM with clean boundaries (UI, ViewModel, Domain, Data).
3. All screens implemented (Home Dashboard, Nearby Devices, Scan Report, Safety Screen, Settings).
4. Unit tests covering observation deduplication, RSSI classification, audio classifier, and statistics calculation.
5. All 6 root documentation files (`PRD.md`, `Architecture.md`, `Rules.md`, `Design.md`, `Task.md`, `Memory.md`) and `README.md` complete and consistent.
