### 🤖 Project Knowledge Base: Avezer

**🎯 Overview:**
*   **Type:** Native Android Mobile Application.
*   **Goal:** (Inferred) A modern mobile app utilizing Compose and network services.
*   **Root Directory:** `D:\Android\Avezer`

**🛠️ Tech Stack & Configuration:**
*   **Language/Platform:** Kotlin, Java 21.
*   **Build System:** Gradle (Kotlin DSL - `.kts`).
*   **UI Framework:** Jetpack Compose (`libs.plugins.kotlin.compose`).
*   **Target SDKs:** `compileSdk` = 36, `targetSdk` = 36, `minSdk` = 24.
*   **Code Style:** Enforced by Ktlint.

**🌐 Key Dependencies (Networking & Data):**
*   **HTTP Client:** Ktor (`ktor-client-core`, `ktor-client-cio`).
*   **Authentication:** Ktor Auth support (`ktor-client-auth`).
*   **Serialization:** KotlinX Serialization (`libs.kotlinx.serialization`) and Gson (`libs.google.gson`).

**📂 Project Structure (High Level):**
*   `D:\Android\Avezer`: Root project files, global Gradle configuration.
*   `D:\Android\Avezer\app`: Application module.
    *   `src/main`: Primary source code (Kotlin/Compose UI).
    *   `src/test`/`androidTest`: Unit and Instrumentation tests.


**🧭 Navigation & Architecture (Engine Module):**
*   **Pattern:** Custom Command/State-based routing system (not standard Android NavController).
*   **Core Components:** `Router` manages the navigation stack (`List<Route>`). Each screen is a self-contained `Route`.
*   **Lifecycle Management:** Screens are managed via `ScreenScope`, which handles state (`StateFlow`), events, and lifecycle disposal (`dispose()`) when popped from the stack.


### 🎨 Design System / UI Kit (`@uikit`)
The application utilizes a custom, robust, and highly modular Jetpack Compose UI component library.

**✨ Theming & Styling:**
*   **Dual Theme Support:** Supports both `LightColorScheme` and `DarkColorScheme` with smooth transitions using `animateColorAsState`.
*   **Design Tokens:** Defines comprehensive tokens:
    *   **Colors (`AppColors`):** Primary accent, background, and content colors for light/dark modes.
    *   **Typography (`AppTypography`):** Consistent text styles (e.g., `headlineLarge`, `bodyMedium`).

**🧱 Core Components:**
The kit provides reusable components integrated with state management:
*   **`Button`:** Custom button handling loading states.
*   **`SentenceRow`:** Versatile row for titles/subtitles, supporting clicks and loading indicators.
*   **`TopAppBar`:** Structured header component with optional title/description and action icons.
*   **`Dialog`:** Themed wrapper around `AlertDialog`.
*   **`ErrorMessage`:** Dedicated composable for displaying errors with dynamic actions (Dismiss, Retry).

**⚙️ Architectural Principles:**
The UI Kit components are not just styled elements; they are integrated into the application's state management system (`AppScreen`), which handles global events like errors and loading states. This ensures high consistency and modularity across the entire interface.


**🚀 Common Commands & Inferences**:

| Command | Description |
|---------|-------------|
| `./gradlew assembleDebug` | Compile and install APK to a device/emulator |
| `adb.exe install -r "./app/build/outputs/apk/debug/app-debug.apk"` | Manually install the debug APK on an ADB device |
| `./gradlew clean` | Remove all build artifacts |
| `./gradlew ktlintFormat` | Format code using Ktlint |
| `./gradlew build` | Full project build with tests |

**💡 Notes:**
*   **Kotlin & Java 21 Toolchain:** The project uses JVM Toolchain to ensure Kotlin and Java tasks both target JVM 21, avoiding compatibility errors.

### Code Update/Addition Principles

1. **Reference Search:** Before making changes, find 3 or more references in the codebase to ensure the correct approach. If none exist, implement the functionality cleanly and without unnecessary abstractions at your discretion.
2. **Error Handling (Suspend Context):** Always handle errors within a `suspend` context, following this structure:
   ```kotlin
   try { 
     some_operation()
   } catch(e: CancellationException) {
     throw e // Re-throw cancellation exception
   } catch(e: Exception) {
     // Handle other errors (logging, returning error, etc.)
   }
   ```
3. **Imports:** Always verify that necessary imports are added for new classes or functions. Use Context7 if dependency checks are required.
4. **Code Safety:** Add code carefully and always check to ensure that new blocks do not accidentally remove already implemented functionality.
