package com.nstrange.arthabit.data.remote

import com.nstrange.arthabit.data.remote.dto.UpdateProfileRequest
import com.nstrange.arthabit.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {

    @GET("/user/v1/users/{userId}")
    suspend fun getUser(
        @Path("userId") userId: String,
        @Header("Authorization") authHeader: String? = null
    ): Response<UserDto>

    @PUT("/user/v1/updateProfile")
    suspend fun updateProfile(
        @Query("userId") userId: String,
        @Header("Authorization") authHeader: String,
        @Body request: UpdateProfileRequest
    ): Response<UserDto>
}
