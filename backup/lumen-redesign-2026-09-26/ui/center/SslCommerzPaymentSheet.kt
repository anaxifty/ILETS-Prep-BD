package com.example.ui.center

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.PartnerCenter
import com.example.data.models.PartnerSlot
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SslCommerzPaymentSheet(
    center: PartnerCenter,
    slot: PartnerSlot,
    studentName: String,
    studentPhone: String,
    tranId: String,
    onPaymentSuccess: (String) -> Unit,
    onPaymentFailed: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMethod by remember { mutableStateOf("bKash") }
    var isProcessing by remember { mutableStateOf(false) }

    val primaryGreen = MaterialTheme.colorScheme.primary

    ModalBottomSheet(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = Color.White,
        modifier = modifier.testTag("sslcommerz_payment_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00658F).copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Secured Payment",
                            tint = Color(0xFF00658F),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "SSLCommerz Secured Payment",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = primaryGreen
                        )
                        Text(
                            text = "Secure Bangladesh Payment Gateway",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }

                IconButton(
                    onClick = { if (!isProcessing) onDismiss() },
                    enabled = !isProcessing,
                    modifier = Modifier.testTag("sslcommerz_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Payment",
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Transaction Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF4F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = slot.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                        Text(
                            text = "৳${slot.priceBdt.toInt()} BDT",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = primaryGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Center: ${center.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "Slot: ${slot.date} (${slot.time})",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = Color.LightGray.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Transaction ID:",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            text = tranId,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Payment Method Selector
            Text(
                text = "Select Payment Method",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = primaryGreen
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PaymentMethodChip(
                    name = "bKash",
                    icon = Icons.Default.PhoneAndroid,
                    isSelected = selectedMethod == "bKash",
                    onSelect = { selectedMethod = "bKash" },
                    modifier = Modifier.weight(1f)
                )
                PaymentMethodChip(
                    name = "Nagad",
                    icon = Icons.Default.PhoneAndroid,
                    isSelected = selectedMethod == "Nagad",
                    onSelect = { selectedMethod = "Nagad" },
                    modifier = Modifier.weight(1f)
                )
                PaymentMethodChip(
                    name = "Cards / Bank",
                    icon = Icons.Default.CreditCard,
                    isSelected = selectedMethod == "Cards / Bank",
                    onSelect = { selectedMethod = "Cards / Bank" },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Disclaimer Banner (Hard Requirement)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFFFE5B0)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFFB26A00),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Booking is for a practice mock test / coaching sitting at partner center. Does NOT register for official IELTS exam.",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF3A2500)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Pay Button / Processing Indicator
            if (isProcessing) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = primaryGreen)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Connecting SSLCommerz Gateway ($selectedMethod)...",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = primaryGreen
                    )
                    Text(
                        text = "Please do not close app during transaction processing",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }

                // Simulate SSLCommerz Gateway network call
                LaunchedEffect(Unit) {
                    delay(2000)
                    onPaymentSuccess(tranId)
                }
            } else {
                Button(
                    onClick = { isProcessing = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("sslcommerz_pay_now_button"),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryGreen)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pay ৳${slot.priceBdt.toInt()} BDT via SSLCommerz",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { onPaymentFailed("Payment cancelled by user.") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Cancel Transaction", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
private fun PaymentMethodChip(
    name: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) else Color.White

    Surface(
        modifier = modifier
            .clickable { onSelect() }
            .border(1.5.dp, borderColor, RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        color = bgColor
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.DarkGray
            )
        }
    }
}
