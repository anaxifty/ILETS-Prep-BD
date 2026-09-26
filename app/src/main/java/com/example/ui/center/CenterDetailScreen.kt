package com.example.ui.center

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import com.example.ui.components.*
import com.example.ui.theme.LumenTheme
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterDetailScreen(
    center: PartnerCenter,
    onBookSlot: (PartnerSlot, String, String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showBookingDialog by remember { mutableStateOf<PartnerSlot?>(null) }
    var studentName by remember { mutableStateOf("") }
    var studentPhone by remember { mutableStateOf("") }

    val darkGreen = LumenTheme.colors.primary // Emerald Focus brand green

    LumenScaffold(
        topBar = {
            LumenTopBar(
                title = {
                    Text(
    text = "Partner Center Details",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = Color.White
)
                },
                navigationIcon = {
                    LumenIconButton(onClick = onBack, modifier = Modifier.testTag("center_detail_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = LumenTopBarDefaults.topAppBarColors(containerColor = darkGreen),
                modifier = Modifier.statusBarsPadding()
            )
        },
        containerColor = LumenTheme.colors.background,
        modifier = modifier.fillMaxSize().testTag("center_detail_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Center Profile Info Header
            item {
                LumenCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = LumenCardDefaults.cardColors(containerColor = darkGreen),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
    text = center.name,
    style = (LumenTheme.typography.titleLarge).copy(fontWeight = FontWeight.Bold),
    color = Color.White
)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = center.address,
                                        style = LumenTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }
                            }

                            // Rating LumenBadge
                            LumenSurface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White.copy(alpha = 0.18f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFF2B968),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
    text = "${center.rating}",
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = FontWeight.Bold),
    color = Color.White
)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = center.description,
                            style = LumenTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        LumenDivider(color = Color.White.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(16.dp))

                        // Contacts
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
    text = center.contactPhone,
    style = (LumenTheme.typography.bodySmall).copy(fontWeight = FontWeight.Bold),
    color = Color.White
)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = center.contactEmail,
                                    style = LumenTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }
            }

            // Practice Test Disclaimer Banner
            item {
                LumenSurface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFCDF4E0)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF1B8A5A),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
    text = "Practice Mock Test & Coaching Slots. Seats reserved via secure SSLCommerz payment.",
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = FontWeight.Medium),
    color = Color(0xFF00341D)
)
                    }
                }
            }

            // Slots Header
            item {
                Text(
    text = "Available Practice Mock Slots",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onBackground
)
            }

            // Slot Cards List
            items(center.slots) { slot ->
                SlotCard(
                    slot = slot,
                    onBookClick = { showBookingDialog = slot }
                )
            }
        }
    }

    // Student Info Modal before SSLCommerz Payment
    showBookingDialog?.let { slot ->
        AlertDialog(
            onDismissRequest = { showBookingDialog = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = darkGreen
                )
            },
            title = {
                Text(
    text = "Confirm Slot Booking",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold)
)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
    text = slot.title,
    style = (LumenTheme.typography.bodyMedium).copy(fontWeight = FontWeight.Bold),
    color = darkGreen
)
                    Text(
                        text = "${slot.date} | ${slot.time}",
                        style = LumenTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
    text = "Price: ৳${slot.priceBdt.toInt()} BDT",
    style = (LumenTheme.typography.bodyMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.primary
)

                    Spacer(modifier = Modifier.height(16.dp))

                    LumenField(
                        value = studentName,
                        onValueChange = { studentName = it },
                        label = { Text("Student Full Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("booking_student_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LumenField(
                        value = studentPhone,
                        onValueChange = { studentPhone = it },
                        label = { Text("Phone Number for SMS Confirmation") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("booking_student_phone_input")
                    )
                }
            },
            confirmButton = {
                LumenButton(
                    onClick = {
                        val sSlot = slot
                        showBookingDialog = null
                        onBookSlot(sSlot, studentName, studentPhone)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = darkGreen),
                    modifier = Modifier.testTag("proceed_to_sslcommerz_button")
                ) {
                    Text("Proceed to SSLCommerz Payment")
                }
            },
            dismissButton = {
                LumenTextButton(onClick = { showBookingDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SlotCard(
    slot: PartnerSlot,
    onBookClick: () -> Unit
) {
    val darkGreen = LumenTheme.colors.primary // Emerald Focus brand green

    LumenCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("center_slot_card_${slot.slotId}"),
        shape = RoundedCornerShape(20.dp),
        colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.surfaceVariant.copy(alpha = 0.5f)),
        elevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LumenSurface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (slot.slotType == "PRACTICE_MOCK") darkGreen.copy(alpha = 0.12f) else Color(0xFF00658F).copy(alpha = 0.12f)
                ) {
                    Text(
    text = if (slot.slotType == "PRACTICE_MOCK") "PRACTICE MOCK TEST" else "COACHING SESSION",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = if (slot.slotType == "PRACTICE_MOCK") darkGreen else Color(0xFF00658F),
    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
)
                }

                Text(
    text = "৳${slot.priceBdt.toInt()} BDT",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = darkGreen
)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
    text = slot.title,
    style = (LumenTheme.typography.titleSmall).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurface
)

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${slot.date} | ${slot.time}",
                    style = LumenTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LumenSurface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (slot.seatsRemaining > 3) Color(0xFFCDF4E0) else Color(0xFFFFDAD6)
                ) {
                    Text(
    text = if (slot.seatsRemaining > 0) "${slot.seatsRemaining} of ${slot.capacity} Seats Available" else "FULLY BOOKED",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = if (slot.seatsRemaining > 3) Color(0xFF1B8A5A) else Color(0xFFBA1A1A),
    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
)
                }

                LumenButton(
                    onClick = onBookClick,
                    enabled = slot.seatsRemaining > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = darkGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("book_slot_button_${slot.slotId}")
                ) {
                    Text(
    text = if (slot.seatsRemaining > 0) "Book Practice Slot" else "Sold Out",
    style = (LumenTheme.typography.labelMedium).copy(fontWeight = FontWeight.Bold)
)
                }
            }
        }
    }
}
