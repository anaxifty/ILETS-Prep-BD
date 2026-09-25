package com.example.data.models

data class PartnerCenter(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val city: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val description: String = "",
    val contactPhone: String = "",
    val contactEmail: String = "",
    val rating: Double = 4.8,
    val slots: List<PartnerSlot> = emptyList()
)

data class PartnerSlot(
    val slotId: String = "",
    val date: String = "",
    val time: String = "",
    val title: String = "",
    val slotType: String = "PRACTICE_MOCK", // "PRACTICE_MOCK" or "COACHING_SESSION"
    val priceBdt: Double = 1500.0,
    val capacity: Int = 20,
    val seatsRemaining: Int = 20
)

data class CenterBooking(
    val bookingId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhone: String = "",
    val centerId: String = "",
    val centerName: String = "",
    val centerAddress: String = "",
    val slotId: String = "",
    val slotDate: String = "",
    val slotTime: String = "",
    val slotTitle: String = "",
    val slotType: String = "",
    val priceBdt: Double = 0.0,
    val paymentStatus: String = "SUCCESS", // "SUCCESS", "FAILED", "PENDING"
    val paymentTranId: String = "",
    val bookingStatus: String = "CONFIRMED", // "CONFIRMED", "COMPLETED", "CANCELLED"
    val createdAtMillis: Long = System.currentTimeMillis()
)
