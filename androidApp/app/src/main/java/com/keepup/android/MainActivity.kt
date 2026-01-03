package com.keepup.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.keepup.android.ui.AppViewModel
import com.keepup.android.ui.screens.*
import com.keepup.android.ui.screens.auth.*
import com.keepup.android.ui.theme.KeepUpTheme
import kotlinx.coroutines.launch

data class TabItem(val route: String, val label: String, val icon: @Composable () -> Unit)

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KeepUpTheme {
                RootScreen(viewModel)
            }
        }
    }
}

@Composable
private fun RootScreen(viewModel: AppViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isSignedIn by viewModel.isSignedIn.collectAsState()

    // Show auth flow if not signed in
    if (!isSignedIn || currentUser == null) {
        AuthFlow(viewModel)
    } else if (currentUser?.isEmailVerified == false) {
        // Show verification screen if email not verified
        val scope = rememberCoroutineScope()
        VerificationScreen(
            email = currentUser?.email ?: "",
            onCheckVerification = {
                scope.launch {
                    viewModel.getAuthManager().reloadUser()
                }
            },
            onResendEmail = {
                scope.launch {
                    viewModel.getAuthManager().sendEmailVerification()
                }
            },
            onSignOut = {
                viewModel.signOut()
            }
        )
    } else {
        // Show main app
        MainApp(viewModel)
    }
}

@Composable
private fun AuthFlow(viewModel: AppViewModel) {
    val navController = rememberNavController()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    NavHost(navController = navController, startDestination = "signin") {
        composable("signin") {
            SignInScreen(
                onSignIn = { email, password ->
                    isLoading = true
                    scope.launch {
                        viewModel.getAuthManager().signInWithEmail(email, password)
                            .onSuccess {
                                isLoading = false
                            }
                            .onFailure {
                                errorMessage = it.message
                                isLoading = false
                            }
                    }
                },
                onGoogleSignIn = {
                    // TODO: Implement Google Sign-In UI flow
                },
                onNavigateToSignUp = {
                    navController.navigate("signup")
                },
                onForgotPassword = {
                    // TODO: Implement forgot password flow
                },
                isLoading = isLoading,
                error = errorMessage
            )
        }

        composable("signup") {
            SignUpScreen(
                onSignUp = { email, password ->
                    isLoading = true
                    scope.launch {
                        viewModel.getAuthManager().signUpWithEmail(email, password)
                            .onSuccess {
                                isLoading = false
                                // Navigation will happen automatically when auth state changes
                            }
                            .onFailure {
                                errorMessage = it.message
                                isLoading = false
                            }
                    }
                },
                onNavigateToSignIn = {
                    navController.popBackStack()
                },
                onTermsClick = {
                    // TODO: Open terms URL
                },
                onPrivacyClick = {
                    // TODO: Open privacy URL
                },
                isLoading = isLoading,
                error = errorMessage
            )
        }
    }
}

@Composable
private fun MainApp(viewModel: AppViewModel) {
    val navController = rememberNavController()
    val tabs = listOf(
        TabItem("updates", "Updates") { Icon(Icons.Default.Tv, contentDescription = null) },
        TabItem("search", "Search") { Icon(Icons.Default.Search, contentDescription = null) },
        TabItem("library", "Library") { Icon(Icons.Default.GridView, contentDescription = null) },
        TabItem("settings", "Settings") { Icon(Icons.Default.Settings, contentDescription = null) }
    )

    Scaffold(
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route
            NavigationBar {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = currentRoute == tab.route,
                        onClick = { 
                            navController.navigate(tab.route) { 
                                launchSingleTop = true 
                                popUpTo("updates") { saveState = true }
                                restoreState = true
                            } 
                        },
                        label = { Text(tab.label) },
                        icon = tab.icon
                    )
                }
            }
        }
    ) { padding ->
        val scope = rememberCoroutineScope()
        NavHost(
            navController = navController, 
            startDestination = "updates", 
            modifier = Modifier.padding(padding)
        ) {
            composable("updates") {
                val updates by viewModel.updates.collectAsState()
                val isLoading by viewModel.isLoadingUpdates.collectAsState()
                UpdatesScreen(
                    updates = updates,
                    onRefresh = { viewModel.refreshUpdates() },
                    onSelect = {
                        viewModel.selectShow(it)
                        navController.navigate("detail")
                    }
                )
            }
            
            composable("search") {
                val results by viewModel.searchResults.collectAsState()
                val isSearching by viewModel.isSearching.collectAsState()
                val error by viewModel.searchError.collectAsState()
                SearchScreen(
                    searchText = viewModel.searchText,
                    searchType = viewModel.searchType,
                    results = results,
                    isLoading = isSearching,
                    error = error,
                    onSearchTextChange = { viewModel.searchText = it },
                    onSearchTypeChange = { viewModel.searchType = it },
                    onSearch = { viewModel.performSearch() },
                    onSelect = {
                        viewModel.selectShow(it)
                        navController.navigate("detail")
                    }
                )
            }
            
            composable("library") {
                val tracked by viewModel.trackedShows.collectAsState()
                LibraryScreen(
                    tracked = tracked,
                    onToggle = { viewModel.toggleTracked(it) },
                    onSelect = {
                        viewModel.selectShow(it)
                        navController.navigate("detail")
                    }
                )
            }
            
            composable("settings") {
                val currentUser by viewModel.currentUser.collectAsState()
                val tracked by viewModel.trackedShows.collectAsState()
                SettingsScreen(
                    trackedCount = tracked.size,
                    onClearTracked = { 
                        tracked.forEach { viewModel.toggleTracked(it) } 
                    }
                )
            }
            
            composable("detail") {
                val selected by viewModel.selectedShow.collectAsState()
                selected?.let { show ->
                    DetailScreen(
                        show = show,
                        isTracked = viewModel.isTracked(show),
                        onToggleTracked = { viewModel.toggleTracked(it) },
                        onLoadDetails = { 
                            viewModel.selectShow(show)
                        },
                        onBack = {
                            viewModel.clearSelection()
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
