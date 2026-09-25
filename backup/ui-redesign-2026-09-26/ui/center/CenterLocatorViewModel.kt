package com.example.ui.center

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.CenterBooking
import com.example.data.models.PartnerCenter
import com.example.data.models.PartnerSlot
import com.example.data.repository.FirestoreRepository
import com.example.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

sealed class PaymentUiState {
    object Idle : PaymentUiState()
    data class PaymentGatewayActive(
        val center: PartnerCenter,
        val slot: PartnerSlot,
        val studentName: String,
        val studentPhone: String,
        val tranId: String
    ) : PaymentUiState()
    object ProcessingBooking : PaymentUiState()
    data class PaymentFailed(val reason: String) : PaymentUiState()
    data class BookingSuccess(val booking: CenterBooking) : PaymentUiState()
}

data class CenterLocatorUiState(
    val isLoading: Boolean = false,
    val allCenters: List<PartnerCenter> = emptyList(),
    val filteredCenters: List<PartnerCenter> = emptyList(),
    val selectedCity: String = "All Cities",
    val searchQuery: String = "",
    val selectedCenter: PartnerCenter? = null,
    val userBookings: List<CenterBooking> = emptyList(),
    val paymentUiState: PaymentUiState = PaymentUiState.Idle,
    val errorMessage: String? = null
)

class CenterLocatorViewModel(
    private val firestoreRepository: FirestoreRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CenterLocatorUiState())
    val uiState: StateFlow<CenterLocatorUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val centers = firestoreRepository.getPartnerCenters()
                val bookings = firestoreRepository.getUserCenterBookings()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        allCenters = centers,
                        filteredCenters = centers,
                        userBookings = bookings
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load partner centers"
                    )
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { state ->
            val filtered = if (query.isBlank()) {
                if (state.selectedCity == "All Cities") state.allCenters
                else state.allCenters.filter { it.city.equals(state.selectedCity, ignoreCase = true) }
            } else {
                state.allCenters.filter { center ->
                    center.name.contains(query, ignoreCase = true) ||
                            center.address.contains(query, ignoreCase = true) ||
                            center.city.contains(query, ignoreCase = true)
                }
            }
            state.copy(searchQuery = query, filteredCenters = filtered)
        }
    }

    fun onCitySelected(city: String) {
        _uiState.update { state ->
            val filtered = if (city == "All Cities") {
                if (state.searchQuery.isBlank()) state.allCenters
                else state.allCenters.filter { center ->
                    center.name.contains(state.searchQuery, ignoreCase = true) ||
                            center.address.contains(state.searchQuery, ignoreCase = true)
                }
            } else {
                state.allCenters.filter { center ->
                    center.city.equals(city, ignoreCase = true) &&
                            (state.searchQuery.isBlank() || center.name.contains(state.searchQuery, ignoreCase = true) || center.address.contains(state.searchQuery, ignoreCase = true))
                }
            }
            state.copy(selectedCity = city, filteredCenters = filtered)
        }
    }

    fun selectCenter(center: PartnerCenter?) {
        _uiState.update { it.copy(selectedCenter = center) }
    }

    fun initiateSSLCommerzPayment(
        center: PartnerCenter,
        slot: PartnerSlot,
        studentName: String,
        studentPhone: String
    ) {
        val tranId = "SSLC_TXN_${System.currentTimeMillis()}"
        _uiState.update {
            it.copy(
                paymentUiState = PaymentUiState.PaymentGatewayActive(
                    center = center,
                    slot = slot,
                    studentName = studentName,
                    studentPhone = studentPhone,
                    tranId = tranId
                )
            )
        }
    }

    fun handleSSLCommerzPaymentSuccess(
        center: PartnerCenter,
        slot: PartnerSlot,
        studentName: String,
        studentPhone: String,
        tranId: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(paymentUiState = PaymentUiState.ProcessingBooking) }

            val profile = userPreferencesRepository.userProfileFlow.first()
            val userId = "user_${System.currentTimeMillis().toString().takeLast(6)}"

            val booking = CenterBooking(
                bookingId = "BOOK_${UUID.randomUUID().toString().take(8).uppercase()}",
                userId = userId,
                userName = studentName.ifEmpty { "Practice Student" },
                userPhone = studentPhone.ifEmpty { profile.phone.ifEmpty { "+8801700000000" } },
                centerId = center.id,
                centerName = center.name,
                centerAddress = center.address,
                slotId = slot.slotId,
                slotDate = slot.date,
                slotTime = slot.time,
                slotTitle = slot.title,
                slotType = slot.slotType,
                priceBdt = slot.priceBdt,
                paymentStatus = "SUCCESS",
                paymentTranId = tranId,
                bookingStatus = "CONFIRMED",
                createdAtMillis = System.currentTimeMillis()
            )

            // Execute atomic Firestore transaction to decrement seatsRemaining and record booking
            val result = firestoreRepository.bookCenterSlotAtomic(booking)
            result.fold(
                onSuccess = { confirmedBooking ->
                    val updatedBookings = firestoreRepository.getUserCenterBookings()
                    val refreshedCenters = firestoreRepository.getPartnerCenters()
                    _uiState.update { state ->
                        state.copy(
                            allCenters = refreshedCenters,
                            filteredCenters = if (state.selectedCity == "All Cities") refreshedCenters else refreshedCenters.filter { it.city.equals(state.selectedCity, ignoreCase = true) },
                            userBookings = updatedBookings,
                            selectedCenter = refreshedCenters.find { it.id == center.id } ?: state.selectedCenter,
                            paymentUiState = PaymentUiState.BookingSuccess(confirmedBooking)
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            paymentUiState = PaymentUiState.PaymentFailed(
                                error.message ?: "Booking failed after payment. Please contact support with Transaction ID: $tranId"
                            )
                        )
                    }
                }
            )
        }
    }

    fun handleSSLCommerzPaymentFailure(reason: String) {
        _uiState.update {
            it.copy(paymentUiState = PaymentUiState.PaymentFailed(reason))
        }
    }

    fun resetPaymentState() {
        _uiState.update { it.copy(paymentUiState = PaymentUiState.Idle) }
    }
}
