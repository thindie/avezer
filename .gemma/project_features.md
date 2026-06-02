# Avezer — Project Features

## 🌦️ Weather Forecast

### 1. Weather Data Fetching
- **API**: Open-Meteo (`api.open-meteo.com`) for weather forecasts
- **Data Points**: Temperature (2m), relative humidity (2m), wind speed, weather codes, isDay flags
- **Mock Data**: Fallback to `MockWeather.create()` when no stored data exists
- **7-Day Forecast**: Configurable forecast length

### 2. Location Resolution
- **Geocoding**: Android `Geocoder` resolves city names → coordinates (lat/lon)
- **Reverse Geocoding**: Coordinates → address data (locality, sub-admin area)
- **Auto-location**: On first fetch, resolves current location and stores weather data

### 3. Weather Data Persistence
- **Local Storage**: `SharedPreferences`-based JSON persistence for weather data
- **Cached Reads**: Loads stored weather data immediately on app launch
- **Background Refresh**: `fetch()` command updates cached data from the network

### 4. Weather Code Mapping
- **Emoji System**: 29 weather codes mapped to contextual emojis (☀️, 🌧️, ❄️, ⚡, etc.)
- **String Resources**: Dynamic weather code text resolution via resource IDs

---

## 🧱 Custom Navigation Engine

### 5. Router (Screen Stack)
- **Push/Pop Navigation**: Custom screen stack management (not `NavController`)
- **Screen Lifecycle**: Automatic disposal (`dispose()`) when screens are popped
- **Batch Removal**: `removeAll(ids)` for clearing multiple screens at once
- **ScreenFlow Controller**: Base class for managing screen flows with `finish()`, `go()`, `back()`

### 6. Route Factory
- **Command/State Pattern**: Each screen is a self-contained `Route` with its own `ScreenScope`
- **Initial Commands**: Screens can execute commands on creation (e.g., auto-fetch on load)
- **Error Mapping**: Per-screen error-to-command mapping with retry/dismiss actions

---

## ⚙️ State Management Architecture

### 7. ScreenScope
- **StateFlow**: Reactive state per screen (`StateFlow<S>`)
- **Processing State**: Tracks which command is currently executing (for loading indicators)
- **Error State**: Per-screen error with actions mapped to commands
- **Event Bus**: `SharedFlow<ServiceCommand.UiEvent>` for dialogs, snackbars, etc.
- **Coroutine Scope**: Per-screen `CoroutineScope` with `SupervisorJob`

### 8. Subscriptions & Transitions
- **`stateSink`**: Declarative subscription builder
- **`sub(flow)`**: Subscribes a `Flow` to screen state
- **`transition()`**: Maps flow emissions to state updates with old/new state comparison

### 9. WorkState
- **Idle / Running / Error**: Simple sealed interface for work status tracking

---

## 🎨 UI Kit (Design System)

### 10. Dual Theme Support
- **Light & Dark Color Schemes**: 9 color tokens each (primary, secondary, accent, background, error, success, etc.)
- **Animated Transitions**: `animateColorAsState` with 400ms tween for smooth theme switching
- **Theme Switcher**: Runtime toggle (Light/Dark/Auto) via `LocalThemeSwitcher`

### 11. Typography System
- **9 Text Styles**: `headlineLarge` (40sp) through `labelMedium` (10sp)
- **Consistent Spacing**: Defined line heights and letter spacings

### 12. Reusable Components
| Component | Features |
|-----------|----------|
| **`Button`** | Loading state, animated colors, disabled state |
| **`SentenceRow`** | Title/subtitle, icon, loading indicator, click/long-click |
| **`TopAppBar`** | Title/description, primary/secondary action icons |
| **`Dialog`** | Themed `AlertDialog` with confirm/dismiss buttons |
| **`ErrorMessage`** | Error display with dismiss/retry buttons |
| **`CircularProgress`** | Themed loading indicator |
| **`AppScreen`** | Wrapper with error handling, dialogs, snackbars, loading overlay |

### 13. UI Events
- **Decision Dialogs**: Custom content with primary/secondary actions
- **Snackbars**: Text-based notifications (auto-dismiss after 2s)
- **Snack with Icon**: Resource reference-based snack messages

### 14. Route Transitions
- **Slide + Fade**: Horizontal slide with fade animation (280ms)
- **Direction-aware**: Different animation for push vs pop

---

## 🌐 Networking Layer

### 15. Retrofit Client
- **Base URL**: `https://api.open-meteo.com/`
- **Gson Serialization**: Request/response parsing
- **Logging Interceptor**: `HttpLoggingInterceptor.Level.BODY` for debug logging
- **Timeouts**: 30s connect and read timeouts

### 16. Error Classification
- **`AppError.TimeOut`**: Timeout errors
- **`AppError.ConnectionFailed`**: Connection failures
- **`AppError.HttpRequestFailed(statusCode)`**: HTTP error codes
- **`AppError.UnexpectedError`**: Catch-all for unexpected exceptions

---

## 🛠️ Engineering Utilities

### 17. Logging (`Log` object)
- **Lazy Evaluation**: Message lambdas only evaluated when logged
- **Levels**: `d()` (debug), `w()` (warning), `e()` (error with throwable)
- **Tag**: `[avezer]` prefix for all logs

### 18. JSON Serialization (`JsonUtil`)
- **Gson-based**: `toJson()` and `fromJson()` utilities
- **Used for**: Persisting weather data to `SharedPreferences`

### 19. Splash Screen
- **Blank/Minimal**: App starts with a blank screen (no dedicated splash UI)

---

## 📦 Architecture Summary

```
MainActivity
├── Router (screen stack)
├── AppTheme (dual theme + animated transitions)
└── AnimatedContent (route transitions)

Application
├── ApplicationScope (DI wiring)
│   ├── StorageImpl (SharedPreferences persistence)
│   ├── ClientImpl (Retrofit + Open-Meteo API)
│   └── LocationResolverImpl (Geocoder)
├── AppFlowModule (feature DI)
│   └── MainRepositoryImpl (weather data source)
└── HomeFlow → places screen

ScreenScope Pattern
├── StateFlow<S> (reactive state)
├── Command execution (suspend functions)
├── Error mapping → retry/dismiss actions
└── Event bus (dialogs, snackbars)
```

---

## ⚠️ Incomplete / Planned Features

1. **Places Screen Content** — `PlacesContent()` has a `BackHandler` but no visible UI yet
2. **WeatherCard** — File exists but not rendered in the UI
3. **Nominatim Fallback** — `TODO("Nominatim fallback")` in `LocationResolverImpl` (not implemented)
4. **Test Coverage** — No `src/test` or `androidTest` directories exist
5. **KotlinX Serialization** — Plugin declared but unused; Gson is used instead
