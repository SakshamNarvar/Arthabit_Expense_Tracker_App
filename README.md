# Arthabit — Android (Kotlin) Frontend

Native Android client for the **Expense Tracker App** microservices ecosystem, built with **Jetpack Compose**, **Hilt**, and **Retrofit**.

## Overview
**Arthabit** is an expense tracker that allows users to sign up, log in (with auto-login), manage expenses, view profile information, and automatically sync bank transaction SMS messages for parsing.

## Tech Stack
- **Languages:** Kotlin 2.0.21
- **UI & Navigation:** Jetpack Compose, Navigation Compose
- **Architecture & DI:** MVVM (Clean Architecture), Dagger Hilt
- **Networking:** Retrofit, OkHttp, Coil
- **Local Storage:** DataStore Preferences
- **Async:** Coroutines, StateFlow

## Architecture
The application follows **Clean Architecture** (MVVM):
- **UI Layer:** Compose screens and Hilt-injected ViewModels using `StateFlow`.
- **Domain Layer:** Pure Kotlin models and repository interfaces (`Auth`, `Expense`, `User`, `Sms`).
- **Data Layer:** Repository implementations, Retrofit APIs (`AuthApi`, `UserApi`, `ExpenseApi`, `DSApi`), DTOs, and DataStore-based `TokenManager`.

## Backend Services
Communicates with four Spring Boot microservices via REST APIs:
- **Auth Service (9898):** Login, signup, token validation/refresh.
- **User Service (9810):** User profile retrieval.
- **Expense Service (9820):** Expense CRUD operations.
- **DS Service (8010):** SMS parsing (`/v1/ds/message`) to auto-extract expenses.

*Configure the IP in `ApiConfig.kt` to target a local emulator (`10.0.2.2`) or physical device connected over LAN.*

## Features & Screens
- **Login / Auto-Login:** Session restoration flows validating existing tokens.
- **Signup:** Account registration with client-side validation.
- **Home:** Lazy-loaded recent expenses, Add Expense floating action bottom sheet. Automatically syncs un-processed SMS for transaction parsing.
- **Profile:** Manage personal information, settings, and logout capabilities.

## Setup & Build
1. Open the project folder in Android Studio.
2. Sync Gradle and ensure the four backend services are running.
3. Select an emulator or physical device.
4. Run `assembleDebug` or click **Run ▶** in Android Studio.

*Note: Requires Android API 35 (compileSdk).*
