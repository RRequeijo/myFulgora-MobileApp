# myFulgora — Electric Motorcycle Companion App

> Native Android companion app for the **Fulgora** electric motorcycle, developed as part of the **A-Mover** academic research project at UTAD (University of Trás-os-Montes e Alto Douro)

---

## Overview

myFulgora connects riders to their electric motorcycle in real time. It displays live battery status, range, telemetry, GPS location, and driving mode — all streamed directly from the bike's onboard computer via gRPC. The app also manages the digital key delegation system, documents, maintenance schedule, and rider profile.

---

## Features

| Area | Description |
|---|---|
| **Dashboard** | Live battery %, range, consumption, driving mode (Eco / Normal / Sport) |
| **Battery** | Detailed battery health, temperature, charging cycles, charge time remaining |
| **Map** | Real-time GPS tracking of the motorcycle |
| **Performance** | Odometer, tyre pressure, average speed, CO₂ saved, trip history |
| **Digital Keys** | Share and revoke access to the motorcycle via email-based delegation |
| **Documentation** | Upload and view PDF documents (insurance, registration) with local cache |
| **Maintenance** | Scheduled service calendar with upcoming and past service records |
| **Settings** | Metric/imperial units, notifications, biometric login, language (EN/PT), theme |
| **Profile** | Edit name, email, and profile photo with optimistic UI updates |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Navigation | Compose Navigation |
| State management | ViewModel + StateFlow / Flow |
| Persistence | Jetpack DataStore (Preferences) |
| Authentication | Keycloak (Resource Owner Password Grant via OkHttp) |
| Biometrics | AndroidX Biometric |
| Real-time data | gRPC (Kotlin Coroutines stubs, Protobuf Lite) |
| Image loading | Coil |
| Maps | Google Maps Compose SDK |
| Min SDK | API 26 (Android 8.0) |
| Target SDK | API 36 |

---

## Project Structure

```
app/src/main/java/com/example/myfulgora/
│
├── data/
│   ├── auth/
│   │   ├── AuthManager.kt        # OkHttp → Keycloak password-grant login
│   │   ├── AuthStorage.kt        # AppAuth state (SharedPreferences)
│   │   └── UserManager.kt        # In-memory session singleton
│   ├── helpers/
│   │   ├── NotificationManager.kt # In-app notification state (Flow)
│   │   ├── SettingsManager.kt    # DataStore wrapper for all preferences
│   │   └── UnitConverter.kt      # km ↔ miles / km/h ↔ mph helpers
│   ├── model/
│   │   ├── BikeState.kt          # Full UI state snapshot for one motorcycle
│   │   ├── FulgoraNotification.kt
│   │   ├── ProfileState.kt
│   │   └── UserModels.kt         # User, UserProfile, Bike data classes
│   ├── remote/
│   │   └── GrpcClass.kt          # gRPC client (one-shot + streaming)
│   └── repository/
│       └── TokenRepository.kt    # DataStore wrapper for auth token
│
├── ui/
│   ├── components/
│   │   ├── FulgoraBackground.kt  # App background with glow effect
│   │   ├── FulgoraUI.kt          # Shared composables (TopBar, Cards, etc.)
│   │   └── FulgoraInputs.kt      # Styled text fields
│   ├── screens/
│   │   ├── SplashScreen.kt
│   │   ├── MainScreen.kt         # Root shell with drawer + bottom nav
│   │   ├── auth/
│   │   │   ├── LoginScreen.kt
│   │   │   ├── ForgotPasswordScreen.kt
│   │   │   └── OnboardingScreen.kt
│   │   └── tabs/
│   │       ├── HomeScreen.kt
│   │       ├── BatteryScreen.kt
│   │       ├── PerformanceScreen.kt
│   │       ├── SettingsScreen.kt
│   │       ├── ProfileScreen.kt
│   │       ├── DelegationScreen.kt
│   │       ├── DocumentationScreen.kt
│   │       ├── MaintenanceScreen.kt
│   │       ├── TripHistoryScreen.kt
│   │       └── map/
│   │           ├── MapScreen.kt
│   │           └── MapStyles.kt
│   ├── theme/
│   │   ├── AppIcons.kt           # Centralised drawable resource constants
│   │   ├── Color.kt
│   │   ├── Dimensions.kt         # Responsive ratios (% of screen width)
│   │   ├── Theme.kt
│   │   └── Type.kt
│   └── viewmodel/
│       ├── ForgotPasswordViewModel.kt
│       ├── LoginViewModel.kt
│       ├── MotaViewModel.kt      # Main bike state + gRPC polling
│       └── ProfileViewModel.kt
│
└── MainActivity.kt               # Single-activity Compose entry point
```

---

## How to Build

### Prerequisites

- Android Studio Hedgehog or newer
- JDK 11
- Android SDK API 36
- A `local.properties` file at the project root (see below)

### local.properties

```properties
sdk.dir=/path/to/your/Android/Sdk
MAPS_API_KEY=YOUR_GOOGLE_MAPS_API_KEY
```

> **Note:** `local.properties` is excluded from version control (`.gitignore`). Each developer must create their own copy.

### Run

1. Clone the repository:
   ```bash
   git clone https://github.com/A-MoVer/myFulgora-MobileApp.git
   ```
2. Open in Android Studio.
3. Add your `local.properties`.
4. Sync Gradle and run on a device or emulator (API 26+).

### Debug Login (Development Only)

While the Keycloak/gRPC backend is not reachable, use the debug bypass:

| Username | Password | Effect |
|---|---|---|
| `test` | `test` | Logs in with a mock user, no network required |

> This bypass is **only active in debug builds** (`BuildConfig.DEBUG = true`) and must be removed before production release.

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                      UI Layer (Compose)                  │
│  Screens ←→ ViewModels (StateFlow) ←→ Shared Components │
└────────────────────────┬────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────┐
│                     Data Layer                           │
│                                                          │
│  ┌─────────────┐  ┌──────────────┐  ┌────────────────┐  │
│  │ AuthManager │  │ SettingsManager│ │ TokenRepository│  │
│  │ (Keycloak)  │  │ (DataStore)  │  │ (DataStore)    │  │
│  └─────────────┘  └──────────────┘  └────────────────┘  │
│                                                          │
│  ┌─────────────┐  ┌──────────────┐                      │
│  │  GrpcClass  │  │ UserManager  │                      │
│  │ (gRPC/Proto)│  │ (In-memory)  │                      │
│  └─────────────┘  └──────────────┘                      │
└─────────────────────────────────────────────────────────┘
```

**Key design decisions:**

- **Single Activity** — `MainActivity` hosts the entire Compose NavGraph.
- **MVVM** — Each screen has a ViewModel that owns state as `StateFlow`; Compose collects via `collectAsState()`.
- **Responsive layout** — `BoxWithConstraints` + `Dimens` percentage ratios ensure the UI adapts to all screen sizes.
- **Offline fallback** — `MotaViewModel` falls back to locally cached `Bike` data when gRPC is unreachable.
- **Localisation** — Full EN/PT string resource coverage; language switchable at runtime via `AppCompatDelegate`.

---

## Localisation

The app ships with two locales:

| Locale | File |
|---|---|
| English (default) | `res/values/strings.xml` |
| Portuguese | `res/values-pt/strings.xml` |

Language can be changed at runtime from **Settings → Language**.

---

## Roadmap

- [ ] **Keycloak full integration** — Replace password-grant with PKCE Authorization Code flow; wire up password recovery endpoints in `ForgotPasswordViewModel`
- [ ] **gRPC TLS** — Switch from `usePlaintext()` to `useTransportSecurity()` with a valid certificate
- [ ] **Encrypted token storage** — Replace `DataStore` plain-text token with `EncryptedSharedPreferences` or Android Keystore
- [ ] **Push notifications** — Replace in-memory `NotificationManager` with FCM-backed server events
- [ ] **Real user data** — Populate `UserManager` with actual user/bike data from server response after login
- [ ] **Proguard / R8** — Enable `isMinifyEnabled = true` in release build with appropriate rules
- [ ] **Unit & UI tests** — Add ViewModel unit tests and Compose UI tests
- [ ] **CI/CD** — GitHub Actions workflow for build, lint, and test on every PR

---

## Security Notes

See [`PLAN.md`](PLAN.md) for the full security audit. Critical items before production:

1. Remove all debug credential bypasses (`test/test`, `amover/amover`)
2. Move `KEYCLOAK_URL`, `GRPC_HOST`, and `GRPC_PORT` to `BuildConfig` fields per build variant
3. Enable TLS on the gRPC channel
4. Store the auth token in `EncryptedSharedPreferences`
5. Set `android:usesCleartextTraffic="false"` in the manifest (after switching all endpoints to HTTPS/TLS)
6. Enable R8/ProGuard in release builds

---

## License

Academic project — A-Mover / UTAD. Not licensed for commercial use.
