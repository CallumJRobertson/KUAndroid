# KUAndroid Quick Start Guide

Quick reference for developers working on the KUAndroid project.

## Project Overview

Android app with complete feature parity to iOS app (CallumJRobertson/KUIOS). Built with:
- Kotlin
- Jetpack Compose
- Firebase (Auth, Firestore, Analytics)
- TMDB API
- Material Design 3

## Setup (5 minutes)

1. **Clone & Open:**
   ```bash
   git clone https://github.com/CallumJRobertson/KUAndroid.git
   cd KUAndroid/androidApp
   # Open in Android Studio
   ```

2. **Firebase Setup:**
   - Get `google-services.json` from Firebase Console
   - Replace placeholder at `app/google-services.json`
   - Use same Firebase project as iOS for cross-platform sync

3. **TMDB API:**
   ```bash
   export TMDB_API_KEY="your_key_here"
   ```

4. **Build & Run:**
   - Sync Gradle
   - Run on device/emulator

## Architecture

### Data Flow
```
UI (Compose) → ViewModel → Repository → TMDB API / Firebase
                ↓
            DataStore (local preferences)
```

### Key Classes

| Class | Purpose | Location |
|-------|---------|----------|
| `KeepUpApplication` | App initialization, Firebase setup | `KeepUpApplication.kt` |
| `AuthManager` | Firebase Authentication | `auth/AuthManager.kt` |
| `FirestoreManager` | Cloud storage, reviews, sync | `firestore/FirestoreManager.kt` |
| `ShowRepository` | TMDB API, show data | `data/ShowRepository.kt` |
| `AppViewModel` | App state, business logic | `ui/AppViewModel.kt` |

### Data Models

All in `data/Models.kt`:
- `Show` - Complete show data (25+ properties)
- `CastMember` - Cast/crew info
- `Review` / `ReviewSummary` - Reviews system
- `WatchProgress` - Episode tracking
- `SeasonInfo` / `EpisodeInfo` - TV show structure

## Design System

### Colors (iOS matching)
```kotlin
Primary = Color(0xFFA855F7)           // Purple accent
BackgroundStart = Color(0xFF0A0C19)   // Gradient start
BackgroundEnd = Color(0xFF141D2E)     // Gradient end
SuccessGreen = Color(0xFF4ADE80)
ErrorRed = Color(0xFFEF4444)
```

### Modifiers
```kotlin
Modifier.glassMorphism()           // Glass effect
Modifier.darkGradientBackground()  // Dark gradient
```

### Typography (iOS matching)
- Display Large: 34sp, Bold (Large Title)
- Display Medium: 28sp, Bold (Title)
- Headline Medium: 17sp, Semibold
- Body Large: 17sp, Regular
- Body Small: 15sp, Regular
- Label Large: 13sp, Regular
- Label Small: 12sp, Regular

## Common Tasks

### Add New Screen

1. Create composable in `ui/screens/`:
   ```kotlin
   @Composable
   fun MyScreen(
       onNavigate: () -> Unit,
       modifier: Modifier = Modifier
   ) {
       Box(
           modifier = modifier
               .fillMaxSize()
               .darkGradientBackground()
       ) {
           // Your UI
       }
   }
   ```

2. Add to navigation in `MainActivity.kt`

### Use Firebase Auth

```kotlin
val authManager = AuthManager(context)

// Sign in
val result = authManager.signInWithEmail(email, password)

// Listen to auth state
authManager.authStateFlow.collect { user ->
    if (user != null) {
        // Signed in
    }
}
```

### Use Firestore

```kotlin
val firestoreManager = FirestoreManager()

// Sync tracked shows
firestoreManager.syncTrackedShows(shows)

// Submit review
firestoreManager.submitReview(review)

// Watch reviews in real-time
firestoreManager.getReviews(showId).collect { reviews ->
    // Update UI
}
```

### Fetch Show Details

```kotlin
val repository = ShowRepository(context, apiKey)

// Search
val results = repository.search("Breaking Bad")

// Get details
val show = repository.fetchDetails(basicShow)

// Get season episodes
val episodes = repository.fetchSeasonDetails(showId, seasonNumber)
```

## UI Components

### PosterView
```kotlin
PosterView(
    posterUrl = show.posterUrl,
    contentDescription = show.title,
    modifier = Modifier.width(150.dp)
)
```

### StarRatingView
```kotlin
StarRatingView(
    rating = 4.5f,
    starSize = 20.dp
)

// Interactive
StarRatingSelector(
    rating = userRating,
    onRatingChange = { newRating -> /* update */ }
)
```

### Glass Morphism Card
```kotlin
Card(
    modifier = Modifier
        .fillMaxWidth()
        .glassMorphism(),
    colors = CardDefaults.cardColors(
        containerColor = SurfaceVariant.copy(alpha = 0.3f)
    )
) {
    // Content
}
```

## Testing

```bash
# Unit tests
./gradlew test

# Instrumentation tests
./gradlew connectedAndroidTest

# Specific test
./gradlew test --tests AuthManagerTest
```

## Debugging

### Enable Firebase Debug Logging
```kotlin
FirebaseFirestore.setLoggingEnabled(true)
```

### Check API Calls
- OkHttp logging interceptor is enabled in debug
- Check Logcat for "OkHttp" tag

### Common Issues

**Build Error: google-services.json**
- Ensure file is properly configured
- Package name must be `com.keepup.android`

**Firebase Not Working**
- Check internet permission in manifest
- Verify Firebase initialization in Application class
- Check Firebase Console for project status

**Images Not Loading**
- Check internet connectivity
- Verify Coil dependency
- Check TMDB image URLs

## Git Workflow

```bash
# Create feature branch
git checkout -b feature/my-feature

# Commit changes
git add .
git commit -m "Description of changes"

# Push
git push origin feature/my-feature

# Create PR on GitHub
```

## Code Style

- Use Kotlin coding conventions
- Follow Material Design 3 guidelines
- Match iOS design as closely as possible
- Add comments for complex logic
- Write descriptive commit messages

## Resources

- [Jetpack Compose Docs](https://developer.android.com/jetpack/compose)
- [Firebase Android Docs](https://firebase.google.com/docs/android/setup)
- [TMDB API Docs](https://developers.themoviedb.org/3)
- [Material Design 3](https://m3.material.io/)
- [iOS App Reference](https://github.com/CallumJRobertson/KUIOS)

## Need Help?

1. Check `IMPLEMENTATION_STATUS.md` for current progress
2. Review `README.md` for detailed setup
3. Look at existing screens for patterns
4. Check iOS app for design reference

## Quick Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Clean build
./gradlew clean

# List tasks
./gradlew tasks

# Generate signing report (for Firebase)
./gradlew signingReport
```

## Performance Tips

1. Use `remember` for expensive computations
2. Use `LazyColumn/LazyRow` for lists
3. Use `derivedStateOf` for derived values
4. Avoid recomposition with `key()`
5. Profile with Android Studio Profiler

## Security Checklist

- ✅ No hardcoded API keys (use BuildConfig)
- ✅ ProGuard rules for release
- ✅ Firebase security rules configured
- ✅ User data encrypted at rest
- ✅ Validate user input
- ✅ Use HTTPS for all network calls

Happy coding! 🚀
