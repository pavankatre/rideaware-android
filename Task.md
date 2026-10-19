# Task Tracking & Implementation Checklist — RideAware

**Status Legend:**
- `TODO`: Pending implementation
- `IN_PROGRESS`: Currently being worked on
- `BLOCKED`: Blocked by external dependency or environment
- `DONE`: Completed and verified

---

## Milestone 0 — Workspace Inspection & Planning
- [x] **TASK-001**: Inspect workspace files and directories. *(Priority: P0 | Status: DONE)*
- [x] **TASK-002**: Inspect installed toolchain (JDK, Gradle, Android SDK, Git). *(Priority: P0 | Status: DONE | Note: Git available; Android SDK & JDK not pre-installed in global CLI PATH)*
- [x] **TASK-003**: Plan architecture and project setup. *(Priority: P0 | Status: DONE)*

## Milestone 1 — Documentation Foundation
- [x] **TASK-101**: Create `PRD.md` with requirements, limitations, and user stories. *(Priority: P0 | Status: DONE)*
- [x] **TASK-102**: Create `Architecture.md` with layer boundaries, state flow, and package layout. *(Priority: P0 | Status: DONE)*
- [x] **TASK-103**: Create `Rules.md` with engineering standards and data ethics. *(Priority: P0 | Status: DONE)*
- [x] **TASK-104**: Create `Design.md` with Material 3 color system and UI layout specifications. *(Priority: P0 | Status: DONE)*
- [x] **TASK-105**: Create `Task.md` implementation checklist. *(Priority: P0 | Status: DONE)*
- [x] **TASK-106**: Create `Memory.md` tracking architecture decisions. *(Priority: P0 | Status: DONE)*
- [x] **TASK-107**: Create `README.md` project guide. *(Priority: P0 | Status: DONE)*

## Milestone 2 — Android Project Foundation & Build System
- [x] **TASK-201**: Create Gradle root configuration (`settings.gradle.kts`, `build.gradle.kts`, `gradle/libs.versions.toml`). *(Priority: P0 | Status: DONE)*
- [x] **TASK-202**: Create Gradle wrapper files (`gradlew`, `gradlew.bat`, `gradle-wrapper.properties`). *(Priority: P0 | Status: DONE)*
- [x] **TASK-203**: Configure `app/build.gradle.kts` with Compose compiler 2.0.21, Material 3, AndroidX Lifecycle, and Navigation. *(Priority: P0 | Status: DONE)*
- [x] **TASK-204**: Configure `AndroidManifest.xml` with BLE permissions and features. *(Priority: P0 | Status: DONE)*
- [x] **TASK-205**: Create `RideAwareApplication.kt` and `MainActivity.kt`. *(Priority: P0 | Status: DONE)*
- [x] **TASK-206**: Implement Material 3 Theme (`Color.kt`, `Theme.kt`, `Type.kt`). *(Priority: P0 | Status: DONE)*
- [x] **TASK-207**: Implement Navigation structure (`AppNavigation.kt`) with bottom navigation bar. *(Priority: P0 | Status: DONE)*

## Milestone 3 — Permissions & Bluetooth Status Management
- [x] **TASK-301**: Implement `PermissionHelper.kt` for Android 12+ and legacy runtime permissions. *(Priority: P0 | Status: DONE)*
- [x] **TASK-302**: Create Bluetooth hardware capability and state observer in `MainActivity.kt`. *(Priority: P0 | Status: DONE)*
- [x] **TASK-303**: Build permission rationale dialogs and banner UI components (`StateBanners.kt`). *(Priority: P0 | Status: DONE)*

## Milestone 4 — BLE Scanner & Domain Logic
- [x] **TASK-401**: Define Domain models (`DiscoveredDevice`, `DeviceCategory`, `SignalStrength`, `ScanReportData`). *(Priority: P0 | Status: DONE)*
- [x] **TASK-402**: Implement `DeviceClassifier.kt` with evidence-based audio indicators. *(Priority: P0 | Status: DONE)*
- [x] **TASK-403**: Implement `BleScannerInterface` and `AndroidBleScanner.kt` with safe ScanCallback and structured coroutine scoping. *(Priority: P0 | Status: DONE)*
- [x] **TASK-404**: Implement `ScanRepository.kt` and `ScanRepositoryImpl.kt` with session deduplication. *(Priority: P0 | Status: DONE)*
- [x] **TASK-405**: Implement `ScanViewModel.kt` with timer management and unidirectional state flow. *(Priority: P0 | Status: DONE)*

## Milestone 5 — Screen Implementations
- [x] **TASK-501**: Implement `HomeScreen.kt` (Dashboard with status badges, live counter, start/stop CTA, and limitation disclaimers). *(Priority: P0 | Status: DONE)*
- [x] **TASK-502**: Implement `NearbyDevicesScreen.kt` (Device cards, signal strength badges, search, sorting, and empty state). *(Priority: P0 | Status: DONE)*
- [x] **TASK-503**: Implement `ScanReportScreen.kt` (Session statistics, signal distribution chart, and data wipe action). *(Priority: P0 | Status: DONE)*
- [x] **TASK-504**: Implement `SafetyScreen.kt` (Stationary-first principles, legal guidance, and anti-confrontation education). *(Priority: P0 | Status: DONE)*
- [x] **TASK-505**: Implement `SettingsScreen.kt` (Scan timeout duration, theme options, cache reset, privacy policy). *(Priority: P0 | Status: DONE)*

## Milestone 6 — Testing & Validation
- [x] **TASK-601**: Write JUnit unit tests for `DeviceClassifier`. *(Priority: P0 | Status: DONE)*
- [x] **TASK-602**: Write unit tests for `SignalStrength` categorization and RSSI formatting. *(Priority: P0 | Status: DONE)*
- [x] **TASK-603**: Write unit tests for device observation deduplication and observation count increments. *(Priority: P0 | Status: DONE)*
- [x] **TASK-604**: Write unit tests for `ScanStatistics` calculations (average RSSI, min/max, distribution). *(Priority: P0 | Status: DONE)*
- [x] **TASK-605**: Document comprehensive manual test plan with real-world scenarios. *(Priority: P0 | Status: DONE)*

## Milestone 7 — Final Polish & Documentation Review
- [x] **TASK-701**: Verify consistency across all documentation files and code symbols. *(Priority: P0 | Status: DONE)*
- [x] **TASK-702**: Prepare handover report detailing build steps, environment requirements, and test instructions. *(Priority: P0 | Status: DONE)*

## Milestone 8 — Device Details & Scope Alignment
- [x] **TASK-801**: Implement `DeviceDetailsDialog.kt` displaying un-fabricated BLE parameters (Name, Android address, Current & Peak RSSI, Timestamps, UUIDs, Manufacturer & Service Data, Classification Evidence). *(Priority: P0 | Status: DONE)*
- [x] **TASK-802**: Update `ObservationCard` with clickable interaction to launch details dialog while leaving "Strongest RSSI" sorting unchanged. *(Priority: P0 | Status: DONE)*
- [x] **TASK-803**: Align project scope: Confirm Clear Session Data & Scan Timeout limits; schedule Export/Share to Post-MVP; reject vibration/haptics for user road safety. *(Priority: P0 | Status: DONE)*
- [x] **TASK-804**: Synchronize documentation across `PRD.md`, `Architecture.md`, `Design.md`, and `Task.md`. *(Priority: P0 | Status: DONE)*
