package com.example.ui.center

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import com.example.ui.components.*
import com.example.ui.theme.LumenTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.PartnerCenter
import com.example.data.models.PartnerSlot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterLocatorScreen(
    viewModel: CenterLocatorViewModel,
    uiState: CenterLocatorUiState,
    onSelectCenter: (PartnerCenter) -> Unit,
    onNavigateToMyBookings: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val darkGreen = LumenTheme.colors.primary // Emerald Focus brand green

    var showMapCanvas by remember { mutableStateOf(true) }

    // Handle SSLCommerz Payment Sheet Modal
    when (val paymentState = uiState.paymentUiState) {
        is PaymentUiState.PaymentGatewayActive -> {
            SslCommerzPaymentSheet(
                center = paymentState.center,
                slot = paymentState.slot,
                studentName = paymentState.studentName,
                studentPhone = paymentState.studentPhone,
                tranId = paymentState.tranId,
                onPaymentSuccess = { tranId ->
                    viewModel.handleSSLCommerzPaymentSuccess(
                        center = paymentState.center,
                        slot = paymentState.slot,
                        studentName = paymentState.studentName,
                        studentPhone = paymentState.studentPhone,
                        tranId = tranId
                    )
                },
                onPaymentFailed = { reason ->
                    viewModel.handleSSLCommerzPaymentFailure(reason)
                },
                onDismiss = {
                    viewModel.resetPaymentState()
                }
            )
        }
        is PaymentUiState.BookingSuccess -> {
            AlertDialog(
                onDismissRequest = { viewModel.resetPaymentState() },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        tint = Color(0xFF1B8A5A)
                    )
                },
                title = { Text("Booking Confirmed!") },
                text = {
                    Column {
                        Text(
                            text = "Your practice slot at ${paymentState.booking.centerName} is confirmed.",
                            style = LumenTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
    text = "Slot: ${paymentState.booking.slotDate} (${paymentState.booking.slotTime})",
    style = (LumenTheme.typography.bodySmall).copy(fontWeight = FontWeight.Bold),
    color = darkGreen
)
                        Text(
                            text = "SSLCommerz TXN: ${paymentState.booking.paymentTranId}",
                            style = LumenTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                },
                confirmButton = {
                    LumenButton(
                        onClick = {
                            viewModel.resetPaymentState()
                            onNavigateToMyBookings()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = darkGreen)
                    ) {
                        Text("View My Bookings")
                    }
                },
                dismissButton = {
                    LumenTextButton(onClick = { viewModel.resetPaymentState() }) {
                        Text("Done")
                    }
                }
            )
        }
        is PaymentUiState.PaymentFailed -> {
            AlertDialog(
                onDismissRequest = { viewModel.resetPaymentState() },
                title = { Text("Payment / Booking Failed") },
                text = { Text(paymentState.reason) },
                confirmButton = {
                    LumenButton(
                        onClick = { viewModel.resetPaymentState() },
                        colors = ButtonDefaults.buttonColors(containerColor = darkGreen)
                    ) {
                        Text("OK")
                    }
                }
            )
        }
        else -> {}
    }

    LumenScaffold(
        topBar = {
            LumenTopBar(
                title = {
                    Column {
                        Text(
    text = "Find Practice Center",
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = Color.White
)
                        Text(
                            text = "Find a center for practice tests & coaching",
                            style = LumenTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    LumenIconButton(onClick = onBack, modifier = Modifier.testTag("center_locator_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    LumenIconButton(
                        onClick = onNavigateToMyBookings,
                        modifier = Modifier.testTag("my_bookings_header_button")
                    ) {
                        LumenBadgeBox(badge = {
                            if (uiState.userBookings.isNotEmpty()) {
                                LumenBadge(containerColor = LumenTheme.colors.error) {
                                    Text("${uiState.userBookings.size}")
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "My Practice Bookings",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = LumenTopBarDefaults.topAppBarColors(containerColor = darkGreen),
                modifier = Modifier.statusBarsPadding()
            )
        },
        containerColor = LumenTheme.colors.background,
        modifier = modifier.fillMaxSize().testTag("center_locator_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // HARD REQUIREMENT PERSISTENT DISCLAIMER NOTE/LINK
            LumenSurface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("official_exam_disclaimer_banner"),
                color = Color(0xFFFFE5B0)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Notice",
                        tint = Color(0xFF8A5A00),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
    text = "PRACTICE MOCK SITTINGS ONLY",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = Color(0xFF8A5A00)
)
                        Text(
                            text = "This locator books practice mock tests and coaching at partner centers. Does NOT register for official IELTS exam.",
                            style = LumenTheme.typography.bodySmall,
                            color = Color(0xFF45524B)
                        )
                    }
                    LumenTextButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.ieltsidpindia.com"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.testTag("official_idp_link_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
    text = "Official IDP",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = Color(0xFF8A5A00)
)
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = null,
                                tint = Color(0xFF8A5A00),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }

            // Search Bar & Filter Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(darkGreen.copy(alpha = 0.05f))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                LumenField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = { Text("Search city, area, or center name...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Gray
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    colors = LumenFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("center_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // City Filter Chips
                val cities = listOf("All Cities", "Dhaka", "Chittagong", "Sylhet")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cities) { city ->
                        LumenChip(
                            selected = uiState.selectedCity == city,
                            onClick = { viewModel.onCitySelected(city) },
                            label = { Text(city) },
                            modifier = Modifier.testTag("city_chip_$city")
                        )
                    }
                }
            }

            // Interactive Map Representation Canvas
            LumenCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .height(160.dp)
                    .testTag("google_maps_canvas_card"),
                shape = RoundedCornerShape(20.dp),
                colors = LumenCardDefaults.cardColors(containerColor = Color(0xFFBFF1D8))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Stylized Map Canvas Graphics
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Map,
                                    contentDescription = null,
                                    tint = darkGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
    text = "Google Maps Partner Center View",
    style = (LumenTheme.typography.titleSmall).copy(fontWeight = FontWeight.Bold),
    color = darkGreen
)
                            }

                            LumenSurface(
                                shape = RoundedCornerShape(10.dp),
                                color = darkGreen
                            ) {
                                Text(
    text = "${uiState.filteredCenters.size} Centers Found",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = Color.White,
    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uiState.filteredCenters.take(3).forEach { center ->
                                LumenSurface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color.White,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onSelectCenter(center) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = darkGreen,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
    text = center.name.split(" ").firstOrNull() ?: center.city,
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    maxLines = 1,
    color = darkGreen
)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // List of Partner Centers
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.filteredCenters) { center ->
                    PartnerCenterCard(
                        center = center,
                        onClick = { onSelectCenter(center) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PartnerCenterCard(
    center: PartnerCenter,
    onClick: () -> Unit
) {
    val darkGreen = LumenTheme.colors.primary // Emerald Focus brand green

    LumenCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("partner_center_card_${center.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = LumenCardDefaults.cardColors(containerColor = LumenTheme.colors.surface),
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
    text = center.name,
    style = (LumenTheme.typography.titleMedium).copy(fontWeight = FontWeight.Bold),
    color = LumenTheme.colors.onSurface
)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = center.address,
                        style = LumenTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LumenSurface(
                        shape = RoundedCornerShape(10.dp),
                        color = darkGreen.copy(alpha = 0.1f)
                    ) {
                        Text(
    text = "${center.slots.sumOf { it.seatsRemaining }} Practice Seats Available",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = darkGreen,
    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFB26A00),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
    text = "${center.rating}",
    style = (LumenTheme.typography.labelSmall).copy(fontWeight = FontWeight.Bold),
    color = Color.DarkGray
)
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View Slots",
                tint = darkGreen,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
