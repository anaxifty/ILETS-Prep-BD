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
import androidx.compose.material3.*
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
    val darkGreen = MaterialTheme.colorScheme.primary // Emerald Focus brand green

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
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Slot: ${paymentState.booking.slotDate} (${paymentState.booking.slotTime})",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = darkGreen
                        )
                        Text(
                            text = "SSLCommerz TXN: ${paymentState.booking.paymentTranId}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                },
                confirmButton = {
                    Button(
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
                    TextButton(onClick = { viewModel.resetPaymentState() }) {
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
                    Button(
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Find Practice Center",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Find a center for practice tests & coaching",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("center_locator_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToMyBookings,
                        modifier = Modifier.testTag("my_bookings_header_button")
                    ) {
                        BadgedBox(badge = {
                            if (uiState.userBookings.isNotEmpty()) {
                                Badge(containerColor = MaterialTheme.colorScheme.error) {
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = darkGreen),
                modifier = Modifier.statusBarsPadding()
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize().testTag("center_locator_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // HARD REQUIREMENT PERSISTENT DISCLAIMER NOTE/LINK
            Surface(
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
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8A5A00)
                        )
                        Text(
                            text = "This locator books practice mock tests and coaching at partner centers. Does NOT register for official IELTS exam.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF45524B)
                        )
                    }
                    TextButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.ieltsidpindia.com"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.testTag("official_idp_link_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Official IDP",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
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
                OutlinedTextField(
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
                    colors = OutlinedTextFieldDefaults.colors(
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
                        FilterChip(
                            selected = uiState.selectedCity == city,
                            onClick = { viewModel.onCitySelected(city) },
                            label = { Text(city) },
                            modifier = Modifier.testTag("city_chip_$city")
                        )
                    }
                }
            }

            // Interactive Map Representation Canvas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .height(160.dp)
                    .testTag("google_maps_canvas_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFBFF1D8))
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
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = darkGreen
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = darkGreen
                            ) {
                                Text(
                                    text = "${uiState.filteredCenters.size} Centers Found",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
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
                                Surface(
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
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
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
    val darkGreen = MaterialTheme.colorScheme.primary // Emerald Focus brand green

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("partner_center_card_${center.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
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
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = darkGreen.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "${center.slots.sumOf { it.seatsRemaining }} Practice Seats Available",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
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
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
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
