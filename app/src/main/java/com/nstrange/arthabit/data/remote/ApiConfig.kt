package com.nstrange.arthabit.data.remote

/**
 * Centralized configuration for all backend service base URLs.
 * Uses 10.0.2.2 for Android emulator to reach host machine's localhost.
 * For physical device testing, replace with your machine's LAN IP.
 */
object ApiConfig {
    private const val HOST = "arthabit-api.sakshamnarvar.tech"
    const val AUTH_SERVICE_URL = "https://$HOST/auth-service/"
    const val USER_SERVICE_URL = "https://$HOST/user-service/"
    const val EXPENSE_SERVICE_URL = "https://$HOST/expense-service/"
    const val DS_SERVICE_URL = "https://$HOST/ds-service/"
}