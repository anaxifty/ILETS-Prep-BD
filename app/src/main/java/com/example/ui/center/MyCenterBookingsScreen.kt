package com.example.ui.center

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Shield
import com.example.ui.components.*
import com.example.ui.theme.LumenTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.models.CenterBooking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCenterBookingsScreen(
    bookings: List<CenterBooking>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val darkGreen = LumenTheme.colors.primary // Emerald Focus brand green

    LumenScaffold(
        topBar = {
            LumenTopBar(
                title = {
                    Text(
    text = "My Practice Bookings",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = Color.White
)
                },
                navigationIcon = {
                    LumenIconButton(onClick = onBack, modifier = Modifier.testTag("my_bookings_back_button")) {
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
        modifier = modifier.fillMaxSize().testTag("my_bookings_screen")
    ) { innerPadding ->
        if (bookings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
    text = "No Practice Slot Bookings Found",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onBackground
)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "You haven't booked any practice mock sittings or coaching sessions yet.",
                        style = LumenTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Disclaimer Notice Box
                    LumenSurface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFFE5B0)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFFB26A00),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Note: These bookings are for practice mock tests at partner centers. They do NOT register you for the official IDP / British Council IELTS exam.",
                                style = LumenTheme.typography.labelSmall,
                                color = Color(0xFF3A2500)
                            )
                        }
                    }
                }

                items(bookings) { booking ->
                    BookingCard(booking = booking)
                }
            }
        }
    }
}

@Composable
private fun BookingCard(booking: CenterBooking) {
    val darkGreen = LumenTheme.colors.primary // Emerald Focus brand green

    LumenCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("booking_card_${booking.bookingId}"),
        shape = RoundedCornerShape(20.dp),
        colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.surface),
        elevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LumenSurface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFCDF4E0)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF1B8A5A),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
    text = booking.bookingStatus,
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = Color(0xFF1B8A5A)
)
                    }
                }

                Text(
                    text = "ID: ${booking.bookingId}",
                    style = LumenTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
    text = booking.slotTitle,
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = darkGreen
)

            Spacer(modifier = Modifier.height(4.dp))

            Text(
    text = booking.centerName,
    style = (LumenTheme.typography.bodyMedium).copy(fontWeight = FontWeight.SemiBold),
    color = LumenTheme.colors.onSurface
)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = booking.centerAddress,
                    style = LumenTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = darkGreen,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
    text = "${booking.slotDate} (${booking.slotTime})",
    style = (LumenTheme.typography.bodySmall).copy(fontWeight = FontWeight.Bold),
    color = darkGreen
)
            }

            Spacer(modifier = Modifier.height(12.dp))
            LumenDivider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color(0xFF00658F),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "SSLCommerz: ${booking.paymentTranId}",
                        style = LumenTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }

                Text(
    text = "৳${booking.priceBdt.toInt()} BDT",
    style = (LumenTheme.typography.titleSmall).copy(fontWeight = FontWeight.Bold),
    color = darkGreen
)
            }
        }
    }
}
