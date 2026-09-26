package com.example.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MarkEmailRead
import com.example.ui.components.*
import com.example.ui.theme.LumenTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerificationScreen(
    viewModel: AuthViewModel,
    uiState: AuthUiState,
    onBack: () -> Unit,
    onVerified: () -> Unit,
    modifier: Modifier = Modifier
) {
    LumenScaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = LumenTheme.colors.background,
        topBar = {
            LumenTopBar(
                title = {},
                navigationIcon = {
                    LumenIconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("otp_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = LumenTheme.colors.onBackground
                        )
                    }
                },
                colors = LumenTopBarDefaults.topAppBarColors(
                    containerColor = LumenTheme.colors.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(LumenTheme.colors.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MarkEmailRead,
                        contentDescription = null,
                        tint = LumenTheme.colors.onPrimaryContainer,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Verify Code",
                    style = LumenTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        letterSpacing = (-0.5).sp
                    ),
                    color = LumenTheme.colors.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Sent 6-digit verification code to ${uiState.phoneNumber}",
                    style = LumenTheme.typography.bodyMedium,
                    color = LumenTheme.colors.onSurfaceVariant
                )
            }

            // OTP Input Box Container
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
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
    text = "ENTER 6-DIGIT OTP",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
    color = LumenTheme.colors.onSurfaceVariant
)

                    Spacer(modifier = Modifier.height(16.dp))

                    LumenField(
                        value = uiState.otpCode,
                        onValueChange = { viewModel.onOtpCodeChanged(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("otp_code_input"),
                        placeholder = {
                            Text(
                                text = "1 2 3 4 5 6",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        singleLine = true,
                        textStyle = LumenTheme.typography.headlineMedium.copy(
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 8.sp,
                            color = LumenTheme.colors.primary
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(20.dp),
                        colors = LumenFieldDefaults.colors(
                            focusedBorderColor = LumenTheme.colors.primary,
                            unfocusedBorderColor = LumenTheme.colors.outline,
                            focusedContainerColor = LumenTheme.colors.surfaceVariant,
                            unfocusedContainerColor = LumenTheme.colors.surfaceVariant
                        )
                    )

                    if (uiState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = uiState.errorMessage,
                            style = LumenTheme.typography.bodySmall,
                            color = LumenTheme.colors.error,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Demo Code Fill Chip
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(LumenTheme.colors.surfaceVariant)
                            .clickable {
                                viewModel.onOtpCodeChanged("123456")
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = LumenTheme.colors.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Auto-fill test OTP (123456)",
                            style = LumenTheme.typography.labelSmall,
                            color = LumenTheme.colors.primary
                        )
                    }
                }
            }

            // Bottom CTA
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LumenButton(
                    onClick = {
                        viewModel.verifyOtp()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("verify_otp_button"),
                    enabled = !uiState.isLoading && uiState.otpCode.length == 6,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LumenTheme.colors.primary,
                        contentColor = LumenTheme.colors.onPrimary
                    )
                ) {
                    if (uiState.isLoading) {
                        LumenSpinner(
                            modifier = Modifier.size(24.dp),
                            color = LumenTheme.colors.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
    text = "Confirm & Proceed",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold)
)
                    }
                }
            }
        }
    }
}
