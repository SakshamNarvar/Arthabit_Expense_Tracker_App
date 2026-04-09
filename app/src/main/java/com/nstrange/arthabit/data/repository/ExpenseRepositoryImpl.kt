package com.nstrange.arthabit.data.repository

import com.nstrange.arthabit.data.local.TokenManager
import com.nstrange.arthabit.data.remote.ExpenseApi
import com.nstrange.arthabit.data.remote.dto.AddExpenseRequest
import com.nstrange.arthabit.data.remote.dto.UpdateExpenseRequest
import com.nstrange.arthabit.domain.model.Expense
import com.nstrange.arthabit.domain.repository.ExpenseRepository
import com.nstrange.arthabit.util.Resource
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val expenseApi: ExpenseApi,
    private val tokenManager: TokenManager
) : ExpenseRepository {

    override suspend fun getExpenses(): Resource<List<Expense>> {
        return try {
            val token = tokenManager.getAccessToken()
                ?: return Resource.Error("No access token")
            val userId = tokenManager.getUserId()
                ?: return Resource.Error("No user ID")

            val response = expenseApi.getExpenses("Bearer $token", userId)
            if (response.isSuccessful) {
                val dtos = response.body() ?: emptyList()
                val expenses = dtos.mapIndexed { index, dto ->
                    Expense(
                        key = index,
                        externalId = dto.externalId,
                        amount = dto.amount,
                        merchant = dto.merchant,
                        currency = dto.currency,
                        notes = dto.notes,
                        category = dto.category,
                        fundSource = dto.fundSource,
                        createdAt = parseDate(dto.createdAt)
                    )
                }
                Resource.Success(expenses)
            } else {
                Resource.Error("Failed to fetch expenses: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun addExpense(
        amount: Double,
        merchant: String,
        currency: String,
        notes: String?,
        category: String?,
        fundSource: String?
    ): Resource<Unit> {
        return try {
            val token = tokenManager.getAccessToken()
                ?: return Resource.Error("No access token")
            val userId = tokenManager.getUserId()
                ?: return Resource.Error("No user ID")

            val request = AddExpenseRequest(
                amount = amount,
                merchant = merchant,
                currency = currency,
                notes = notes,
                category = category,
                fundSource = fundSource
            )

            val response = expenseApi.addExpense(
                authHeader = "Bearer $token",
                userId = userId,
                request = request
            )
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("Failed to add expense: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun updateExpense(
        externalId: String,
        amount: Double?,
        merchant: String?,
        currency: String?,
        notes: String?,
        category: String?,
        fundSource: String?
    ): Resource<Unit> {
        return try {
            val token = tokenManager.getAccessToken()
                ?: return Resource.Error("No access token")

            val request = UpdateExpenseRequest(
                amount = amount,
                merchant = merchant,
                currency = currency,
                notes = notes,
                category = category,
                fundSource = fundSource
            )

            val response = expenseApi.updateExpense(
                authHeader = "Bearer $token",
                externalId = externalId,
                request = request
            )
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("Failed to update expense: ${response.code()}")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    private fun parseDate(dateString: String): LocalDateTime {
        return try {
            LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)
        } catch (e: Exception) {
            LocalDateTime.now()
        }
    }
}
