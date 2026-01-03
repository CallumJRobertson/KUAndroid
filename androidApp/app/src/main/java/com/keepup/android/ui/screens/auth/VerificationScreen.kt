package com.keepup.android.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keepup.android.ui.theme.*

/**
 * Email Verification screen matching iOS design
 * - Envelope icon
 * - "Verify your Email" title
 * - Email display
 * - "I've Verified My Email" button (refreshes auth state)
 * - "Resend Email" button
 * - Status messages
 * - "Sign Out" button
 */
@Composable
fun VerificationScreen(
    email: String,
    onCheckVerification: () -> Unit,
    onResendEmail: () -> Unit,
    onSignOut: () -> Unit,
    isLoading: Boolean = false,
    message: String? = null,
    modifier: Modifier = Modifier
) {
    var showMessage by remember { mutableStateOf(false) }
    
    LaunchedEffect(message) {
        if (message != null) {
            showMessage = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .darkGradientBackground()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.5f))
            
            // Envelope icon
            Text(
                text = "✉️",
                fontSize = 80.sp
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Title
            Text(
                text = "Verify your Email",
                style = MaterialTheme.typography.displayMedium,
                color = OnBackground,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Description
            Text(
                text = "We've sent a verification email to:",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Email
            Text(
                text = email,
                style = MaterialTheme.typography.bodyLarge,
                color = OnBackground,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Instructions
            Text(
                text = "Please check your inbox and click the verification link.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            // Status message
            if (showMessage && message != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .glassMorphism(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (message.contains("verified", ignoreCase = true))
                            SuccessGreen.copy(alpha = 0.2f)
                        else
                            Primary.copy(alpha = 0.2f)
                    )
                ) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnBackground,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // I've Verified My Email button
            Button(
                onClick = onCheckVerification,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor = OnPrimary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = OnPrimary
                    )
                } else {
                    Text("I've Verified My Email", fontWeight = FontWeight.SemiBold)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Resend Email button
            OutlinedButton(
                onClick = onResendEmail,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = OnBackground
                )
            ) {
                Text("Resend Email", fontWeight = FontWeight.SemiBold)
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Sign Out button
            TextButton(
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Sign Out",
                    color = ErrorRed,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
