package com.nstrange.arthabit.domain.repository

import com.nstrange.arthabit.domain.model.Expense
import com.nstrange.arthabit.util.Resource

interface ExpenseRepository {
    suspend fun getExpenses(): Resource<List<Expense>>
    suspend fun addExpense(
        amount: Double,
        merchant: String,
        currency: String,
        notes: String? = null,
        category: String? = null,
        fundSource: String? = null
    ): Resource<Unit>

    suspend fun updateExpense(
        externalId: String,
        amount: Double? = null,
        merchant: String? = null,
        currency: String? = null,
        notes: String? = null,
        category: String? = null,
        fundSource: String? = null
    ): Resource<Unit>
}
