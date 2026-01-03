package com.keepup.android.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keepup.android.ui.theme.*

/**
 * Sign Up screen matching iOS design
 * - Dark gradient background
 * - "Create your account" title
 * - Terms and Privacy Policy agreement checkbox
 * - Legal links displayed
 * - Email and password fields
 * - Agreement checkbox with green checkmark when selected
 * - "Create Account" button
 * - Navigate to verification after signup
 */
@Composable
fun SignUpScreen(
    onSignUp: (email: String, password: String) -> Unit,
    onNavigateToSignIn: () -> Unit,
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    isLoading: Boolean = false,
    error: String? = null,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var agreedToTerms by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    val passwordsMatch = password == confirmPassword
    val canSubmit = email.isNotBlank() && 
                    password.isNotBlank() && 
                    passwordsMatch && 
                    agreedToTerms && 
                    password.length >= 6

    Box(
        modifier = modifier
            .fillMaxSize()
            .darkGradientBackground()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            // Branding
            Text(
                text = "✨",
                fontSize = 60.sp,
                color = Primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "sparkles.tv",
                style = MaterialTheme.typography.displayMedium,
                color = OnBackground,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Create account text
            Text(
                text = "Create your account",
                style = MaterialTheme.typography.headlineMedium,
                color = OnBackground
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
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
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) 
                    VisualTransformation.None 
                else 
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) 
                                Icons.Default.Visibility 
                            else 
                                Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) 
                                "Hide password" 
                            else 
                                "Show password",
                            tint = TextSecondary
                        )
                    }
                },
                supportingText = {
                    if (password.isNotEmpty() && password.length < 6) {
                        Text("Password must be at least 6 characters", color = ErrorRed)
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
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Confirm password field
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
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (canSubmit) {
                            onSignUp(email, password)
                        }
                    }
                ),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) 
                                Icons.Default.Visibility 
                            else 
                                Icons.Default.VisibilityOff,
                            contentDescription = if (confirmPasswordVisible) 
                                "Hide password" 
                            else 
                                "Show password",
                            tint = TextSecondary
                        )
                    }
                },
                supportingText = {
                    if (confirmPassword.isNotEmpty() && !passwordsMatch) {
                        Text("Passwords don't match", color = ErrorRed)
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
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Terms and Privacy agreement
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { agreedToTerms = !agreedToTerms },
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = if (agreedToTerms) 
                        Icons.Default.CheckBox 
                    else 
                        Icons.Default.CheckBoxOutlineBlank,
                    contentDescription = if (agreedToTerms) "Agreed" else "Not agreed",
                    tint = if (agreedToTerms) SuccessGreen else TextSecondary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                val annotatedText = buildAnnotatedString {
                    append("I agree to the ")
                    pushStringAnnotation(tag = "terms", annotation = "terms")
                    withStyle(
                        style = SpanStyle(
                            color = Primary,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("Terms of Service")
                    }
                    pop()
                    append(" and ")
                    pushStringAnnotation(tag = "privacy", annotation = "privacy")
                    withStyle(
                        style = SpanStyle(
                            color = Primary,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("Privacy Policy")
                    }
                    pop()
                }
                Text(
                    text = annotatedText,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnBackground,
                    modifier = Modifier.clickable {
                        // Handle link clicks
                        // This is simplified - actual implementation would parse annotations
                    }
                )
            }
            
            // Error message
            if (error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = ErrorRed,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Create Account button
            Button(
                onClick = { onSignUp(email, password) },
                enabled = !isLoading && canSubmit,
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
                    Text("Create Account", fontWeight = FontWeight.SemiBold)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Sign in link
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                TextButton(onClick = onNavigateToSignIn) {
                    Text(
                        text = "Sign In",
                        color = Primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
