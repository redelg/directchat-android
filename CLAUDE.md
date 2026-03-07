# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
./gradlew test                   # Run unit tests
./gradlew connectedAndroidTest   # Run instrumented tests
./gradlew app:testDebugUnitTest  # Run single module unit tests
```

Build requires Java 17. RevenueCat API keys are read from `local.properties` (key: `REVENUECAT_API_KEY`) or environment variables, with a test fallback for debug builds.

## Architecture

Single-module Android app using **MVVM + Jetpack Compose**. The app lets users open WhatsApp conversations from phone numbers without saving contacts.

### Layer Structure

- **`ui/main/`** — All UI lives here. Single `MainActivity` hosts a Compose tree rooted at `MainRoot` → `MainScaffold`. Tab navigation (Chat, History, Messages, Settings) uses `AnimatedContent` with a `MainTab` enum. Each tab is a composable file (`ChatTab.kt`, `HistoryTab.kt`, `MessagesTab.kt`, `SettingsTab.kt`). Secondary screens (QR, legal, onboarding) are in `SecondaryScreens.kt` and `OnboardingScreen.kt`.
- **`data/`** — Room database (v3) with `ChatDB` (phone number history) and `MessageDB` (saved message templates) entities. `ChatRepository` provides the data access layer. DAOs use Flow for reactive queries. Preferences in `data/preferences/` handle user state and monetization flags.
- **`monetization/`** — RevenueCat for IAP, Google Mobile Ads for banners/rewarded ads. `RevenueCatManager` handles purchases, `MonetizationUiConfig` controls feature flags, `RewardedAdManager` handles ad-based 24h premium unlocks. Free tier: 5 message templates + ads; premium: unlimited templates + no ads.

### Key Patterns

- State management: `MainComposeViewModel` with `StateFlow`, composition-local state via `rememberSaveable`
- Database singleton via `ChatDatabase.getDatabase(context)`
- Repository pattern wrapping Room DAOs
- Country code picker uses `AndroidView` interop for the `ccp` library
- `App.kt` initializes Google Mobile Ads and RevenueCat on startup

## Tech Stack

- **Kotlin 2.1.0**, Java 17 target, KSP for annotation processing
- **Compose BOM 2026.02.00**, Material Design 3
- **Room 2.8.4** (database), **Retrofit 3.0.0** (networking)
- **RevenueCat 9.22.2** (IAP), **Google Ads 25.0.0**
- Compile SDK 36, Min SDK 24

## Theme

Custom Material3 theme in `MainTheme.kt` with primary green (#29AA6F). Custom Avenir font family (assets). Supports Spanish (es-419) and Portuguese (pt) localization via `values-es/` and `values-pt/` string resources.
