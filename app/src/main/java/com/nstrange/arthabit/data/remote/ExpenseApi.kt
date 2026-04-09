package com.nstrange.arthabit.data.remote

import com.nstrange.arthabit.data.remote.dto.AddExpenseRequest
import com.nstrange.arthabit.data.remote.dto.ExpenseDto
import com.nstrange.arthabit.data.remote.dto.UpdateExpenseRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ExpenseApi {

    @GET("/expense/v1/getExpense")
    suspend fun getExpenses(
        @Header("Authorization") authHeader: String,
        @Header("X-User-Id") userId: String
    ): Response<List<ExpenseDto>>

    @POST("/expense/v1/addExpense")
    suspend fun addExpense(
        @Header("Authorization") authHeader: String,
        @Header("X-User-Id") userId: String,
        @Body request: AddExpenseRequest
    ): Response<ResponseBody>

    @POST("/expense/v1/updateExpense")
    suspend fun updateExpense(
        @Header("Authorization") authHeader: String,
        @Header("X-External-ID") externalId: String,
        @Body request: UpdateExpenseRequest
    ): Response<ResponseBody>
}
