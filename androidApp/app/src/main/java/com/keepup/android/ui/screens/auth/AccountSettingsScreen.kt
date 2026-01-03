package com.keepup.android.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.keepup.android.ui.theme.*

/**
 * Account Settings screen matching iOS design
 * - Form layout with sections:
 *   - Account info with verification badge
 *   - "Refresh" and "Resend Email" buttons
 *   - Change Email section
 *   - Change Password section
 *   - Sign Out button
 *   - Danger Zone with Delete Account
 */
@Composable
fun AccountSettingsScreen(
    email: String,
    isEmailVerified: Boolean,
    onRefreshVerification: () -> Unit,
    onResendEmail: () -> Unit,
    onUpdateEmail: (newEmail: String) -> Unit,
    onUpdatePassword: (newPassword: String) -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean = false,
    message: String? = null,
    modifier: Modifier = Modifier
) {
    var newEmail by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .darkGradientBackground()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp)
        ) {
            // Header
            Text(
                text = "Account Settings",
                style = MaterialTheme.typography.displayMedium,
                color = OnBackground,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Current Email Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassMorphism(),
                colors = CardDefaults.cardColors(
                    containerColor = SurfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Current Email",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = OnBackground
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodyLarge,
                            color = OnBackground,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (isEmailVerified) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = SuccessGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    if (!isEmailVerified) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Email not verified",
                            style = MaterialTheme.typography.bodySmall,
                            color = WarningOrange
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onRefreshVerification,
                                enabled = !isLoading,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Refresh", style = MaterialTheme.typography.bodySmall)
                            }
                            OutlinedButton(
                                onClick = onResendEmail,
                                enabled = !isLoading,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Resend Email", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
            
            // Status message
            if (message != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Primary.copy(alpha = 0.2f)
                    )
                ) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnBackground,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Change Email Section
            Text(
                text = "Change Email",
                style = MaterialTheme.typography.headlineMedium,
                color = OnBackground,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = newEmail,
                onValueChange = { newEmail = it },
                label = { Text("New Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .glassMorphism(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = OnBackground,
                    unfocusedTextColor = OnBackground,
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = GlassBorder
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = {
                    if (newEmail.isNotBlank()) {
                        onUpdateEmail(newEmail)
                        newEmail = ""
                    }
                },
                enabled = !isLoading && newEmail.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor = OnPrimary
                )
            ) {
                Text("Update Email")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Change Password Section
            Text(
                text = "Change Password",
                style = MaterialTheme.typography.headlineMedium,
                color = OnBackground,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) 
                    VisualTransformation.None 
                else 
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) 
                                Icons.Default.Visibility 
                            else 
                                Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .glassMorphism(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = OnBackground,
                    unfocusedTextColor = OnBackground,
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = GlassBorder
                )
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                singleLine = true,
                visualTransformation = if (confirmPasswordVisible) 
                    VisualTransformation.None 
                else 
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) 
                                Icons.Default.Visibility 
                            else 
                                Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .glassMorphism(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = OnBackground,
                    unfocusedTextColor = OnBackground,
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = GlassBorder
                )
            )
            
            if (newPassword.isNotEmpty() && confirmPassword.isNotEmpty() && newPassword != confirmPassword) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Passwords don't match",
                    color = ErrorRed,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = {
                    if (newPassword.isNotBlank() && newPassword == confirmPassword) {
                        onUpdatePassword(newPassword)
                        newPassword = ""
                        confirmPassword = ""
                    }
                },
                enabled = !isLoading && 
                         newPassword.isNotBlank() && 
                         newPassword == confirmPassword &&
                         newPassword.length >= 6,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor = OnPrimary
                )
            ) {
                Text("Update Password")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Sign Out button
            OutlinedButton(
                onClick = onSignOut,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = OnBackground
                )
            ) {
                Text("Sign Out")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Danger Zone
            Text(
                text = "Danger Zone",
                style = MaterialTheme.typography.headlineMedium,
                color = ErrorRed,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassMorphism(),
                colors = CardDefaults.cardColors(
                    containerColor = ErrorRed.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Delete Account",
                        style = MaterialTheme.typography.bodyLarge,
                        color = ErrorRed,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This action cannot be undone. All your data will be permanently deleted.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showDeleteDialog = true },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ErrorRed,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Delete My Account")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account?") },
            text = { Text("Are you sure you want to delete your account? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteAccount()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorRed
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
