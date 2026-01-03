# KeepUp Android App

Android implementation mirroring the iOS app (CallumJRobertson/KUIOS) with complete feature parity.

## Setup Instructions

### Prerequisites
- Android Studio (Arctic Fox or newer)
- JDK 17
- Android SDK API 34
- Firebase account
- TMDB API key

### Firebase Configuration

1. **Create/Configure Firebase Project:**
   - Go to [Firebase Console](https://console.firebase.google.com/)
   - Select or create your Firebase project
   - **Important:** Use the same Firebase project as the iOS app for cross-platform data sync

2. **Add Android App to Firebase:**
   - Click "Add app" and select Android
   - Enter package name: `com.keepup.android`
   - Download `google-services.json`
   - Replace the placeholder file at `androidApp/app/google-services.json` with your downloaded file

3. **Enable Firebase Services:**
   - Enable Authentication (Email/Password and Google Sign-In)
   - Enable Cloud Firestore
   - Enable Analytics (optional)
   - Configure Google Sign-In:
     - Add SHA-1 fingerprint from your debug/release keystore
     - Enable Google Sign-In provider in Authentication settings

4. **Configure Web Client ID for Google Sign-In:**
   - In Firebase Console, go to Authentication > Sign-in method > Google
   - Copy the Web client ID
   - You'll need this for Google Sign-In integration

### TMDB API Configuration

1. Get your API key from [TMDB](https://www.themoviedb.org/settings/api)
2. Set the environment variable:
   ```bash
   export TMDB_API_KEY="your_api_key_here"
   ```
   Or add it to your `~/.bash_profile` or `~/.zshrc`

### Building the App

1. **Clone the repository:**
   ```bash
   git clone https://github.com/CallumJRobertson/KUAndroid.git
   cd KUAndroid/androidApp
   ```

2. **Open in Android Studio:**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the `androidApp` directory

3. **Sync Gradle:**
   - Let Android Studio sync Gradle files
   - Install any missing SDK components if prompted

4. **Build and Run:**
   - Select a device/emulator
   - Click Run (Shift+F10)

### Build Variants

- **Debug:** Development build with logging enabled
- **Release:** Production build with ProGuard/R8 optimization

## Architecture

### Core Components

- **Data Models:** Kotlin data classes in `data/Models.kt`
- **Authentication:** Firebase Auth integration in `auth/AuthManager.kt`
- **TMDB Client:** API client in `network/TmdbService.kt`
- **App State:** ViewModel-based state management in `ui/AppViewModel.kt`
- **UI:** Jetpack Compose screens in `ui/screens/`
- **Theme:** Material3 theme matching iOS in `ui/theme/`

### Key Features

1. **Authentication System:**
   - Email/password sign-in and sign-up
   - Google Sign-In
   - Email verification
   - Account management

2. **Show Tracking:**
   - Search movies and TV shows via TMDB
   - Track shows with notifications
   - View show details, cast, watch providers
   - Episode tracking for TV shows

3. **Updates Feed:**
   - Recently released episodes
   - Upcoming episodes
   - Personalized based on tracked shows

4. **Reviews System:**
   - Submit and view reviews
   - Star ratings
   - Spoiler tags
   - Helpful/Not Helpful votes

5. **Notifications:**
   - Configurable notification preferences
   - Scheduled notifications for new episodes
   - WorkManager-based scheduling

6. **Settings:**
   - Theme customization
   - Notification preferences
   - Account management
   - Bug reporting

### UI Design

The app closely mirrors the iOS design with:
- Dark purple gradient backgrounds (#0A0C19 to #141D2E)
- Purple accent color (#A855F7)
- Glass morphism effects
- Smooth animations (300ms default)
- Material Design 3 components

## Testing

Run tests with:
```bash
./gradlew test
./gradlew connectedAndroidTest
```

## Troubleshooting

### Build Errors

1. **Google Services Plugin Error:**
   - Ensure `google-services.json` is properly configured
   - Check that the package name matches: `com.keepup.android`

2. **TMDB API Key Missing:**
   - Set the `TMDB_API_KEY` environment variable
   - Restart Android Studio after setting the variable

3. **Firebase Errors:**
   - Verify Firebase configuration
   - Check internet connectivity
   - Ensure Firebase services are enabled in console

### Runtime Issues

1. **Google Sign-In Not Working:**
   - Add SHA-1 fingerprint to Firebase console
   - Enable Google Sign-In in Authentication settings
   - Use correct Web client ID

2. **Notifications Not Working:**
   - Grant notification permission
   - Check notification settings in app
   - Verify WorkManager is scheduling correctly

## Development Status

This implementation includes:
- ✅ Core data models with all properties
- ✅ Firebase Authentication integration
- ✅ AuthManager with all methods
- ✅ Enhanced theme matching iOS
- ✅ Glass morphism effects
- ✅ Typography matching iOS specs
- ✅ Reusable UI components (PosterView, StarRatingView)
- ✅ Updated dependencies (Firebase, Google Sign-In, WorkManager)
- 🚧 Authentication UI screens (SignIn, SignUp, Verification, AccountSettings)
- 🚧 Enhanced TMDB client
- 🚧 Firestore integration for cloud sync
- 🚧 Enhanced UI screens
- 🚧 Reviews system
- 🚧 Notifications system
- 🚧 Additional screens and features

## Contributing

1. Follow the existing code style
2. Write tests for new features
3. Update documentation
4. Ensure builds pass before submitting PR

## License

[Add License Information]
