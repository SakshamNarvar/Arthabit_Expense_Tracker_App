package com.nstrange.arthabit.domain.repository

import com.nstrange.arthabit.util.Resource

interface SmsRepository {
    suspend fun syncUnprocessedSms(): Resource<Unit>
}

