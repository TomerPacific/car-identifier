# Car Identifier - Copilot Instructions

## Project Overview
Car Identifier is an Android mobile application that allows users to gather information about cars either from images or license plate numbers. The app uses ML Kit for text recognition and translation, and features a Jetpack Compose UI. Published on Google Play Store.

## Project Stats
- **Type**: Android mobile application
- **Language**: Kotlin (100%)
- **Target SDK**: Android API 36 (minSdk: 24)
- **Build System**: Gradle 8.11.1 with Kotlin DSL
- **Framework**: Jetpack Compose with Material 3
- **Code Size**: ~2,400 lines of Kotlin code across 30 source files
- **Package**: com.tomerpacific.caridentifier

## Technology Stack
- **Kotlin**: 2.0.0
- **Compose**: Material 3 (1.3.1) with BOM 2023.03.00
- **Networking**: Ktor 2.3.12 (client-core, client-android, content-negotiation)
- **Serialization**: KotlinX Serialization JSON 1.5.0
- **Camera**: AndroidX CameraX 1.4.1 (core, camera2, view, lifecycle)
- **ML Kit**: Text Recognition 16.0.1, Translate 17.0.3
- **Image Loading**: Coil Compose 2.2.2
- **Animation**: Lottie Compose 6.6.2
- **Navigation**: Navigation Compose 2.7.7
- **Testing**: JUnit 4.13.2, AndroidX JUnit 1.2.1, Espresso 3.6.1

## Build & Runtime Requirements
- **Java/JDK**: 17 (Temurin distribution recommended)
- **Gradle**: 8.11.1 (managed by wrapper - use `./gradlew` always)
- **Android SDK**: Must be installed with build-tools and platform-tools
- **ANDROID_HOME**: Must be set to Android SDK location

## Critical Build Information

### ⚠️ IMPORTANT: Android Gradle Plugin Version Issue
The project uses Android Gradle Plugin (AGP) version 8.9.1 in `gradle/libs.versions.toml`. This version may not be publicly available yet and can cause build failures in local environments with error:
```
Plugin [id: 'com.android.application', version: '8.9.1', apply: false] was not found
```

**Workaround**: If you encounter this error locally, the GitHub Actions CI environment has proper network access and the build WILL succeed in CI. Do NOT modify the AGP version unless explicitly instructed. The CI build validates all changes.

### Build Commands (CI-Validated Sequence)

**ALWAYS run these commands in this exact order:**

1. **Make gradlew executable** (required first step):
   ```bash
   chmod +x ./gradlew
   ```

2. **Run detekt linting** (takes ~2-3 minutes first time, ~30-60 seconds after):
   ```bash
   ./gradlew detekt
   ```

3. **Run unit tests** (takes ~1-2 minutes):
   ```bash
   ./gradlew test
   ```

4. **Build the project** (takes ~3-5 minutes first time, ~1-2 minutes after):
   ```bash
   ./gradlew assemble
   ```

**Complete build from clean state**: Expect 5-10 minutes total on first run due to Gradle daemon initialization and dependency downloads. Subsequent builds are faster (~2-3 minutes).

### Build Artifacts Locations
- **APKs**: `app/build/outputs/apk/debug/app-debug.apk`
- **AAB (Release)**: `app/build/outputs/bundle/release/app-release.aab` (via `./gradlew bundleRelease`)
- **Test Results**: `app/build/test-results/`
- **Detekt Reports**: `build/reports/detekt/` (HTML and XML)

## Project Structure

### Root Directory Files
- `build.gradle.kts` - Root build configuration with detekt setup
- `settings.gradle.kts` - Project settings, includes `:app` module
- `gradle.properties` - Gradle properties (Xmx2048m, AndroidX enabled)
- `gradle/libs.versions.toml` - Version catalog for all dependencies
- `gradlew` / `gradlew.bat` - Gradle wrapper scripts
- `README.md` - Project documentation
- `app_gif.gif` - Demo animation (1.2MB)

### Source Code Layout
```
app/src/main/java/com/tomerpacific/caridentifier/
├── MainActivity.kt                    # Main activity with Navigation setup
├── CameraUtils.kt                     # Camera utility functions
├── LanguageTranslator.kt              # ML Kit translation wrapper
├── Utilities.kt                       # General utility functions
├── model/
│   ├── MainViewModel.kt               # Primary ViewModel (handles network, state)
│   ├── CarDetails.kt                  # Data model for car information
│   ├── CarReview.kt                   # Data model for car reviews
│   ├── Screen.kt                      # Navigation screen definitions
│   └── ServerError.kt                 # Error model
├── data/
│   ├── MainUiState.kt                 # UI state sealed class
│   ├── network/
│   │   ├── AppHttpClient.kt           # Ktor HTTP client configuration
│   │   └── ConnectivityObserver.kt    # Network connectivity monitoring
│   └── repository/
│       ├── CarDetailsRepository.kt    # Data repository for car details
│       └── CarLicensePlateSource.kt   # License plate data source
├── composable/
│   ├── CameraPreview.kt               # Camera preview composable
│   ├── CarAdvice.kt                   # Car advice display
│   ├── CarDetailWithIcon.kt           # Detail item with icon
│   ├── CarInformation.kt              # Car information display
│   ├── CarLicensePlateSearchOptionButton.kt
│   ├── CarReviews.kt                  # Reviews display
│   └── LoaderAnimation.kt             # Lottie loading animation
├── screen/
│   ├── MainScreen.kt                  # Main app screen
│   ├── CarDetailsScreen.kt            # Car details screen
│   ├── HandleCameraPermission.kt      # Camera permission handling
│   ├── HandleGalleryPicker.kt         # Gallery picker screen
│   ├── LicensePlateNumberDialog.kt    # License plate input dialog
│   └── VerifyPhotoDialog.kt           # Photo verification dialog
└── ui/theme/
    ├── Theme.kt                       # Material theme configuration
    ├── Color.kt                       # Color definitions
    └── Type.kt                        # Typography definitions
```

### Resources
- `app/src/main/res/` - Android resources (6MB total)
- `app/src/main/AndroidManifest.xml` - App manifest with permissions (CAMERA, INTERNET, ACCESS_NETWORK_STATE)

### Testing
- `app/src/test/java/com/tomerpacific/caridentifier/UtilitiesUnitTest.kt` - Unit tests for utilities
- Test runner: `androidx.test.runner.AndroidJUnitRunner`

### Configuration Files
- `config/detekt/detekt.yml` - Detekt linting configuration
- `config/detekt/baseline.xml` - Detekt baseline (48 lines total, including a trailing blank line, 41 suppressed violations - mostly FunctionNaming and LongMethod issues for Compose functions)
- `app/proguard-rules.pro` - ProGuard rules (mostly default)
- `.gitignore` - Git ignore rules (excludes build/, .gradle/, *.apk, local.properties, etc.)

## Linting & Code Quality

### Detekt (Static Analysis)
**Configuration**: `config/detekt/detekt.yml`
- Scans: `app/src/main/java` and `app/src/main/kotlin`
- Baseline: `config/detekt/baseline.xml` (41 suppressed violations)
- Parallel execution enabled
- Generates HTML and XML reports

**Run detekt**:
```bash
./gradlew detekt
```

**Common Issues in Baseline** (acceptable as per project standards):
- `FunctionNaming` - Compose functions start with uppercase (Compose convention) - 19 violations
- `MagicNumber` - Color hex codes and numeric constants - 9 violations
- `LongMethod` - Some Compose functions exceed length threshold (acceptable for UI) - 7 violations
- `TooGenericExceptionCaught` - Generic exception handling - 3 violations
- `MaxLineLength` - Lines exceeding length limit - 3 violations
- Total: 41 suppressed violations in baseline

### ktlint (Code Formatting)
**Plugin**: org.jlleitschuh.gradle.ktlint v12.1.1
- Includes Compose-specific rules (io.nlopez.compose.rules:ktlint v0.4.4)
- No custom configuration file

**Note**: ktlint runs automatically with detekt. There is no separate ktlint command exposed in the build.

## CI/CD Pipeline

### Pull Request Validation (android_build.yml)
Runs on: Every pull request
Steps (in order):
1. Checkout code
2. Set up JDK 17 (Temurin distribution) with Gradle cache
3. `chmod +x ./gradlew`
4. `./gradlew detekt`
5. `./gradlew test`
6. `./gradlew assemble`

**All PR changes MUST pass these checks before merge.**

### Release Publishing (android_publish.yml)
Triggers: When `android_build.yml` succeeds on `release/**` branches
Steps:
1. Build release AAB: `./gradlew bundleRelease`
2. Sign AAB with keystore
3. Deploy to Google Play Store (production track)

## Making Code Changes

### Architecture Pattern
- **MVVM**: ViewModel (MainViewModel) manages state and business logic
- **Repository Pattern**: CarDetailsRepository, CarLicensePlateSource
- **Unidirectional Data Flow**: ViewModel → UI State → Compose UI
- **Navigation**: Jetpack Navigation Compose with Screen sealed class

### Key Conventions
1. **Compose Functions**: PascalCase naming (suppressed in detekt baseline)
2. **Data Classes**: Used for models (CarDetails, CarReview, ServerError)
3. **Sealed Classes**: Used for UI state (MainUiState) and screens (Screen)
4. **Coroutines**: Used for async operations (Ktor, ML Kit)
5. **State Management**: ViewModel with LiveData/StateFlow patterns

### Permissions Required
- `CAMERA` - For license plate photo capture
- `INTERNET` - For car details API calls
- `ACCESS_NETWORK_STATE` - For connectivity monitoring

### Adding Dependencies
1. Update `gradle/libs.versions.toml` with version in `[versions]` section
2. Add library reference in `[libraries]` section
3. Reference in `app/build.gradle.kts` using `libs.` prefix
4. Run `./gradlew assemble` to download and verify

### Testing Strategy
- Unit tests for utility functions (UtilitiesUnitTest.kt)
- Tests use JUnit 4 assertions
- Test naming: Use backticks for descriptive test names
- Example: `` `should return true when license plate is valid` ``

## Common Pitfalls & Solutions

### ❌ Build Failures
**Problem**: "Plugin com.android.application version 8.9.1 not found"
**Solution**: This is expected locally. Push to PR - CI will build successfully.

**Problem**: "Android SDK not found"
**Solution**: Ensure `ANDROID_HOME` environment variable is set and points to Android SDK.

**Problem**: "Gradle daemon failed"
**Solution**: Increase heap size in `gradle.properties` (already set to 2048m) or kill daemon: `./gradlew --stop`

### ❌ Lint Failures
**Problem**: "FunctionNaming: Composable function should start with lowercase"
**Solution**: This is a false positive for Compose. Functions starting with uppercase are correct per Compose convention and are in the baseline.

**Problem**: "New detekt issues not in baseline"
**Solution**: Fix the issues OR if they are Compose-related and acceptable, add to baseline: `./gradlew detektBaseline`

### ❌ Test Failures
**Problem**: Hebrew text rendering in tests
**Solution**: Utilities handle Hebrew-to-English translation. Use translated car manufacturer names (e.g., "ניסאן" → "Nissan").

## Development Workflow

1. **Before making changes**: 
   - Run `./gradlew detekt` and `./gradlew test` to establish baseline
   - Note any pre-existing failures (there should be none)

2. **While making changes**:
   - Make minimal, focused changes
   - Follow existing code patterns in similar files
   - Use existing composables as templates

3. **After making changes**:
   - Run `./gradlew detekt` (fix new issues, compare to baseline)
   - Run `./gradlew test` (ensure all tests pass)
   - Run `./gradlew assemble` (ensure build succeeds)

4. **Before pushing**:
   - Verify changes are minimal and necessary
   - Check that no unintended files are committed
   - Trust CI to perform final validation

## Trust These Instructions

These instructions are comprehensive and validated. Only search for additional information if:
- These instructions are incomplete for your specific task
- You encounter an error not documented here
- You need to understand code logic (not build/test process)

For build, test, and lint operations, **follow these instructions exactly** rather than searching.
