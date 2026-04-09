package com.nstrange.arthabit.data.repository

import android.content.Context
import android.provider.Telephony
import com.nstrange.arthabit.data.local.TokenManager
import com.nstrange.arthabit.data.remote.DSApi
import com.nstrange.arthabit.data.remote.dto.DsMessageRequest
import com.nstrange.arthabit.domain.repository.SmsRepository
import com.nstrange.arthabit.util.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dsApi: DSApi,
    private val tokenManager: TokenManager
) : SmsRepository {

    override suspend fun syncUnprocessedSms(): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val userId = tokenManager.getUserId() ?: return@withContext Resource.Error("User not logged in")
            val lastSyncedTime = tokenManager.getLastSyncedSmsTimestamp()

            val cursor = context.contentResolver.query(
                Telephony.Sms.Inbox.CONTENT_URI,
                arrayOf(Telephony.Sms.Inbox.BODY, Telephony.Sms.Inbox.DATE),
                "${Telephony.Sms.Inbox.DATE} > ?",
                arrayOf(lastSyncedTime.toString()),
                "${Telephony.Sms.Inbox.DATE} ASC"
            )

            var latestTimestamp = lastSyncedTime

            cursor?.use {
                val bodyIndex = it.getColumnIndexOrThrow(Telephony.Sms.Inbox.BODY)
                val dateIndex = it.getColumnIndexOrThrow(Telephony.Sms.Inbox.DATE)

                while (it.moveToNext()) {
                    val body = it.getString(bodyIndex) ?: continue
                    val date = it.getLong(dateIndex)

                    try {
                        val request = DsMessageRequest(message = body)
                        dsApi.parseSms(userId, request)

                        // Ignoring parsing failures for now
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    if (date > latestTimestamp) {
                        latestTimestamp = date
                    }
                }
            }

            tokenManager.saveLastSyncedSmsTimestamp(latestTimestamp)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to sync SMS")
        }
    }
}
