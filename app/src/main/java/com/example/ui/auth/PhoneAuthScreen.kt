package com.example.ui.auth

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import com.example.ui.components.*
import com.example.ui.theme.LumenTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneAuthScreen(
    viewModel: AuthViewModel,
    uiState: AuthUiState,
    onNavigateToOtp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scrollState = rememberScrollState()

    LumenScaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = LumenTheme.colors.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                // Category Chip / Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(LumenTheme.colors.primaryContainer)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
    text = "🇧🇩 BANGLADESH EDITION",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = LumenTheme.colors.onPrimaryContainer
)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Start Your IELTS Journey",
                    style = LumenTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        letterSpacing = (-0.5).sp
                    ),
                    color = LumenTheme.colors.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Sign in to personalize your study plan, track band progress, and access full mock exams.",
                    style = LumenTheme.typography.bodyMedium,
                    color = LumenTheme.colors.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Auth Mode Toggle (Phone / Email)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(LumenTheme.colors.surfaceVariant)
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (uiState.authMode == AuthMode.PHONE) LumenTheme.colors.primary
                            else Color.Transparent
                        )
                        .clickable { viewModel.setAuthMode(AuthMode.PHONE) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (uiState.authMode == AuthMode.PHONE) LumenTheme.colors.onPrimary
                            else LumenTheme.colors.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
    text = "Phone OTP",
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = FontWeight.Bold),
    color = if (uiState.authMode == AuthMode.PHONE) LumenTheme.colors.onPrimary
                            else LumenTheme.colors.onSurfaceVariant
)
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (uiState.authMode == AuthMode.EMAIL) LumenTheme.colors.primary
                            else Color.Transparent
                        )
                        .clickable { viewModel.setAuthMode(AuthMode.EMAIL) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (uiState.authMode == AuthMode.EMAIL) LumenTheme.colors.onPrimary
                            else LumenTheme.colors.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
    text = "Email Sign In",
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = FontWeight.Bold),
    color = if (uiState.authMode == AuthMode.EMAIL) LumenTheme.colors.onPrimary
                            else LumenTheme.colors.onSurfaceVariant
)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Input LumenCard Container
            LumenCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = LumenTheme.colors.outline,
                        shape = RoundedCornerShape(26.dp)
                    ),
                shape = RoundedCornerShape(26.dp),
                colors = LumenCardDefaults.cardColors(
                    containerColor = LumenTheme.colors.surface
                ),
                elevation = 0.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    if (uiState.authMode == AuthMode.PHONE) {
                        // Phone Auth Mode
                        Text(
    text = "MOBILE NUMBER",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = LumenTheme.colors.onSurfaceVariant
)

                        Spacer(modifier = Modifier.height(10.dp))

                        LumenField(
                            value = uiState.phoneNumber,
                            onValueChange = { viewModel.onPhoneNumberChanged(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("phone_number_input"),
                            placeholder = { Text("017XXXXXXXX") },
                            leadingIcon = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(start = 12.dp, end = 8.dp)
                                ) {
                                    Text(
    text = "🇧🇩 +880",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.SemiBold),
    color = LumenTheme.colors.onSurface
)
                                    Box(
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .width(1.dp)
                                            .height(20.dp)
                                            .background(LumenTheme.colors.outline)
                                    )
                                }
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = LumenTheme.colors.onSurfaceVariant
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            shape = RoundedCornerShape(20.dp),
                            colors = LumenFieldDefaults.colors(
                                focusedBorderColor = LumenTheme.colors.primary,
                                unfocusedBorderColor = LumenTheme.colors.outline,
                                focusedContainerColor = LumenTheme.colors.surfaceVariant,
                                unfocusedContainerColor = LumenTheme.colors.surfaceVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Autofill demo chip
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(LumenTheme.colors.surfaceVariant)
                                .clickable {
                                    viewModel.onPhoneNumberChanged("01700000000")
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = LumenTheme.colors.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Autofill demo number (01700000000)",
                                style = LumenTheme.typography.labelSmall,
                                color = LumenTheme.colors.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        LumenButton(
                            onClick = {
                                activity?.let { viewModel.sendOtp(it) }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("send_otp_button"),
                            enabled = !uiState.isLoading && uiState.phoneNumber.length >= 10,
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LumenTheme.colors.primary,
                                contentColor = LumenTheme.colors.onPrimary
                            )
                        ) {
                            if (uiState.isLoading) {
                                LumenSpinner(
                                    modifier = Modifier.size(20.dp),
                                    color = LumenTheme.colors.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
    text = "Get Verification OTP",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold)
)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        // Email Auth Mode
                        Text(
    text = "EMAIL ADDRESS",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = LumenTheme.colors.onSurfaceVariant
)

                        Spacer(modifier = Modifier.height(8.dp))

                        LumenField(
                            value = uiState.email,
                            onValueChange = { viewModel.onEmailChanged(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("email_input"),
                            placeholder = { Text("student@example.com") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = LumenTheme.colors.onSurfaceVariant
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            shape = RoundedCornerShape(20.dp),
                            colors = LumenFieldDefaults.colors(
                                focusedBorderColor = LumenTheme.colors.primary,
                                unfocusedBorderColor = LumenTheme.colors.outline,
                                focusedContainerColor = LumenTheme.colors.surfaceVariant,
                                unfocusedContainerColor = LumenTheme.colors.surfaceVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
    text = "PASSWORD",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = LumenTheme.colors.onSurfaceVariant
)

                        Spacer(modifier = Modifier.height(8.dp))

                        LumenField(
                            value = uiState.password,
                            onValueChange = { viewModel.onPasswordChanged(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("password_input"),
                            placeholder = { Text("••••••••") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = LumenTheme.colors.onSurfaceVariant
                                )
                            },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            shape = RoundedCornerShape(20.dp),
                            colors = LumenFieldDefaults.colors(
                                focusedBorderColor = LumenTheme.colors.primary,
                                unfocusedBorderColor = LumenTheme.colors.outline,
                                focusedContainerColor = LumenTheme.colors.surfaceVariant,
                                unfocusedContainerColor = LumenTheme.colors.surfaceVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        LumenButton(
                            onClick = { viewModel.signInWithEmail() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("email_signin_button"),
                            enabled = !uiState.isLoading && uiState.email.isNotEmpty() && uiState.password.isNotEmpty(),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LumenTheme.colors.primary,
                                contentColor = LumenTheme.colors.onPrimary
                            )
                        ) {
                            if (uiState.isLoading) {
                                LumenSpinner(
                                    modifier = Modifier.size(20.dp),
                                    color = LumenTheme.colors.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
    text = "Sign In with Email",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold)
)
                            }
                        }
                    }

                    if (uiState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = uiState.errorMessage,
                            style = LumenTheme.typography.bodySmall,
                            color = LumenTheme.colors.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // LumenDivider "OR"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = LumenTheme.colors.outlineVariant
                )
                Text(
    text = "OR CONTINUE WITH",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.SemiBold),
    color = LumenTheme.colors.onSurfaceVariant,
    modifier = Modifier.padding(horizontal = 12.dp)
)
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = LumenTheme.colors.outlineVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Google Sign-In LumenButton
            LumenOutlinedButton(
                onClick = { viewModel.signInWithGoogle() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("google_signin_button"),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = LumenTheme.colors.surface
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "G",
                        style = LumenTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = LumenTheme.colors.primary
                        )
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
    text = "Sign in with Google",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.SemiBold),
    color = LumenTheme.colors.onSurface
)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Guest / Quick Skip Sign-In LumenButton
            LumenOutlinedButton(
                onClick = { viewModel.signInAsGuest() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("guest_signin_button"),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = LumenTheme.colors.secondaryContainer.copy(alpha = 0.5f)
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = LumenTheme.colors.onSecondaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
    text = "Continue as Guest / Quick Skip",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.SemiBold),
    color = LumenTheme.colors.onSecondaryContainer
)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "By continuing, you agree to our terms of service and privacy policy.",
                style = LumenTheme.typography.bodySmall,
                color = LumenTheme.colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

