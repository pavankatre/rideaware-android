# Visual Design System & UI Specifications — RideAware

**Design Philosophy:** Road-Safety Focused, Calm, High-Legibility, Non-Alarmist, Material 3.

---

## 1. Color Palette & Semantics

The palette uses a modern, deep navy primary foundation paired with energetic teal and restrained warning amber. It strictly avoids alarming reds for benign Bluetooth device observations.

### 1.1 Color Tokens

| Token Name | Light Theme Hex | Dark Theme Hex | Semantic Usage |
|---|---|---|---|
| `Primary` | `#1A2B4C` (Deep Navy) | `#8AB4F8` (Soft Slate Blue) | App bars, prominent CTA buttons, active tabs |
| `OnPrimary` | `#FFFFFF` | `#0A1C36` | Text/icons on primary surfaces |
| `PrimaryContainer`| `#D6E3FF` | `#1E355B` | Highlight cards, active container highlights |
| `Secondary` | `#006A6A` (Deep Teal) | `#4DDAD5` (Vibrant Teal) | Live scan indicators, active badges |
| `SecondaryContainer`| `#CCE8E6`| `#004F4F` | Filter chips, metadata tag backgrounds |
| `Background` | `#F8F9FC` (Off-white) | `#0F141C` (Deepest Charcoal)| Screen background |
| `Surface` | `#FFFFFF` | `#19202E` (Layered Navy Card)| Cards, bottom sheets, elevated containers |
| `SurfaceVariant` | `#E1E2EC` | `#252D3D` | Divider lines, subtle border outlines |
| `WarningAmber` | `#B45309` (Amber 700) | `#FBBF24` (Amber 400) | Safety notices, disclaimers, permission warnings |
| `SignalGood` | `#059669` (Emerald 600) | `#34D399` (Emerald 400) | RSSI >= -65 dBm |
| `SignalFair` | `#D97706` (Amber 600) | `#FCD34D` (Amber 300) | -85 dBm <= RSSI < -65 dBm |
| `SignalWeak` | `#6B7280` (Neutral Grey)| `#9CA3AF` (Light Grey) | RSSI < -85 dBm |

---

## 2. Typography Scale

Utilizes system Sans-Serif font (Roboto) optimized for quick glanceability while stationary:

- **HeadlineLarge (28sp, SemiBold):** Main screen headers ("RideAware Dashboard", "Nearby Devices").
- **TitleMedium (18sp, Medium):** Card titles, device primary names.
- **BodyMedium (14sp, Normal):** Informational disclaimers, explanatory text.
- **LabelSmall (11sp, Bold):** RSSI badges, protocol tags, timestamp indicators.

---

## 3. Elevation & Corner Radii

- **Cards:** 16dp rounded corners (`RoundedCornerShape(16.dp)`), 2dp subtle tonal elevation.
- **Badges & Chips:** 8dp pill shape (`RoundedCornerShape(8.dp)`).
- **Primary Buttons:** 24dp pill shape or 12dp rounded rectangle, minimum 48dp height for comfortable touch targets.

---

## 4. UI Screen Blueprints

### 4.1 Home Dashboard
- **Top Bar:** App title with status chip indicating Bluetooth status (Ready / Off / Scanning).
- **Safety Hero Banner:** Calm, restrained notice: *"Always park your vehicle safely before checking your screen."*
- **Primary Action Panel:** Large scan button with stateful pulsing teal ring when active.
- **Live Counter Grid:** 2-column metrics showing "Devices Observed" and "Active Scan Time".
- **Limitation Disclaimer Card:** Expandable explanation of BLE range and physical constraints.

### 4.2 Nearby Devices Screen
- **Filter & Sort Bar:** Horizontally scrollable row: "Audio Indicators" filter chip, "Strongest RSSI" sort chip (in-place sorting toggle, does not navigate), and "Most Active" sort chip.
- **Clickable Device Card:**
  - Header: Neutral icon + Device Name (e.g. "DEV-A492" or "Sony WH-1000XM4").
  - Signal Badge: Dynamic RSSI badge (e.g. `● Good (-62 dBm)`).
  - Metadata Row: Observation count, first/last seen delta, advertised service indicators.
  - Classification Tag: `Unknown BLE Device` or `Possible Audio Indicator`.
  - Interaction: Tapping the card opens the **Device Details Inspection Dialog**.
- **Device Details Dialog (`DeviceDetailsDialog.kt`):**
  - Displays real, un-fabricated data: Device Name, Android-restricted address, Current & Peak RSSI, Observation count, First/Last seen timestamps, Advertised Service UUIDs, Manufacturer & Service Data hex payloads, and Audio classification evidence.
  - Explicit scientific caveats noting that RSSI denotes RF signal power rather than physical distance, and audio indicators do not prove earphone usage or rider distraction.
  - Clean `Close Details` button and top `X` icon to return smoothly to the unchanged device list.

### 4.3 Scan Report Screen
- **Summary Header:** Total session duration and total unique device count.
- **Signal Distribution Breakdown:** Visual percentage bar showing Good, Fair, and Weak signal counts.
- **Limitations & Ethics Notice:** Persistent card reminding user that observations reflect only discoverable BLE packets at that exact location.
- **Action:** "Export Session Summary (Local Text)" or "Clear Session Observations".

### 4.4 Safety Screen
- 5 Core Safety Pillars (Park First, Don't Confront, Physics Limitations, Legal Awareness, Attention Forward).
- Available at all times even when Bluetooth is turned off.

### 4.5 Settings Screen
- Theme picker (System / Dark / Light).
- Scan Timeout selection (15s, 30s, 60s, Continuous).
- Clear session cache button.
- App Version and Open Source / Privacy information.
