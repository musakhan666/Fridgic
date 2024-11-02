package com.company.foodflow.data.model

import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

data class InventoryItem(
    val id: String = "", // Unique identifier for Firestore document
    val name: String = "",
    val location: String = "",
    val quantity: Int = 0,
    val expirationDate: String = "",
    val imageUrl: String? = null

) {
    private val maxDaysThreshold = 100L // Define a threshold for scaling

    val daysRemaining: Long
        get() {
            return try {
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val expiration = formatter.parse(expirationDate)
                val today = Calendar.getInstance().time
                val diffInMillis = expiration.time - today.time
                TimeUnit.MILLISECONDS.toDays(diffInMillis)
            } catch (e: Exception) {
                Long.MAX_VALUE // Treat invalid dates as very far in the future
            }
        }

    // Calculate progress based on a max threshold
    fun getExpirationProgress(): Float {
        return when {
            daysRemaining <= 0 -> 1.0f // Fully expired
            daysRemaining >= maxDaysThreshold -> 0.1f // Minimal progress for far-off dates
            else -> 1 - (daysRemaining.toFloat() / maxDaysThreshold) // Scale inversely
        }
    }

    // Expiration progress bar color based on days remaining
    val expirationColor: Color
        get() = when {
            daysRemaining <= 2 -> Color.Red
            daysRemaining <= 7 -> Color(0xFFFFA500) // Orange
            daysRemaining <= 14 -> Color(0xFF00CED1) // Turquoise
            else -> Color(0xFF228B22) // Green
        }
}
