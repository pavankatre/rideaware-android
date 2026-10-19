# Engineering & AI Collaboration Rules — RideAware

These rules are strictly enforced across all code creation, modification, and architectural decisions for the RideAware codebase.

---

## 1. Architectural Integrity & Separation of Concerns
1. **No BLE APIs in Composable Functions:** Composable files must never import `android.bluetooth.*`. All BLE interactions occur exclusively within `data/bluetooth/` and are exposed to the UI via `ViewModel` `StateFlow`.
2. **Unidirectional Data Flow (UDF):** UI components emit events up to ViewModels; ViewModels expose immutable `StateFlow<UiState>` models down to the UI.
3. **No God Classes:** Keep classes focused, single-purpose, and modular. Split large screens into sub-composables.
4. **Structured Concurrency:** Always launch asynchronous jobs within managed scopes (`viewModelScope` or lifecycle-aware Coroutine scopes). Never use `GlobalScope`.

---

## 2. Platform & Hardware Safety
1. **Never Crash on Disabled Bluetooth:** Always inspect `BluetoothAdapter.isEnabled` and BLE feature support (`hasSystemFeature(FEATURE_BLUETOOTH_LE)`). Handle disabled states gracefully.
2. **Explicit Resource Cleanup:** All active `ScanCallback` instances must be unregistered when scanning stops, when timed out, or when the lifecycle terminates.
3. **Handle Permission Revocation:** Never assume a permission granted in a previous run remains granted. Always re-verify runtime permissions before initiating hardware calls.
4. **Zero Bypass:** Never attempt to bypass Android permission checks or suppress security exceptions.

---

## 3. Product Authenticity & Data Ethics
1. **Never Falsify Evidence:** Under no circumstances should a nearby device be labeled as a "confirmed distracted rider" or "confirmed active earphone in use".
2. **No Fabricated Mock Data in Production UI:** When zero devices are discovered, display an honest, informative empty state. Never invent simulated Bluetooth devices to populate the UI.
3. **RSSI Transparency:** Always show RSSI in dBm with a qualifying disclaimer that RF signal strength is non-linear and not equivalent to physical distance.
4. **Strict Non-Goals:** Do not write code attempting RF jamming, packet sniffing, audio interception, or unprompted background tracking.

---

## 4. Privacy-by-Design
1. **Session-Scoped Identifiers:** Obfuscate real MAC addresses into transient session keys (e.g. `DEV-A9B2`) to prevent persistent surveillance.
2. **Zero Cloud Telemetry:** Do not transmit nearby radio observations off the device.
3. **Session Purge:** Provide instant, accessible capability for users to wipe all current session observations.

---

## 5. Development Quality & Testing
1. **Meaningful Unit Tests:** Write tests verifying real edge cases (deduplication, audio UUID classification, empty states, RSSI boundary ranges).
2. **No Faked Passing Tests:** Never assert `true == true` or skip test assertions.
3. **Document Deviations:** Any deviation from these rules must be clearly documented with rationale in `Memory.md`.
