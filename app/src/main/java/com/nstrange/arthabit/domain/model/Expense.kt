package com.nstrange.arthabit.domain.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class Expense(
    val key: Int,
    val externalId: String? = null,
    val amount: Double,
    val merchant: String,
    val currency: String,
    val notes: String? = null,
    val category: String? = null,
    val fundSource: String? = null,
    val createdAt: LocalDateTime
) {
    val formattedDate: String
        get() = createdAt.format(DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH))

    val formattedAmount: String
        get() = "$currency %.2f".format(amount)
}
