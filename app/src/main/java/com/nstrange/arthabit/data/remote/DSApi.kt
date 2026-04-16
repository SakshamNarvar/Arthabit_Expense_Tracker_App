package com.nstrange.arthabit.data.remote

import com.nstrange.arthabit.data.remote.dto.DsMessageRequest
import com.nstrange.arthabit.data.remote.dto.DsMessageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface DSApi {

    @POST("v1/ds/message")
    suspend fun parseSms(
        @Header("x-user-id") userId: String,
        @Body request: DsMessageRequest
    ): Response<DsMessageResponse>
}

