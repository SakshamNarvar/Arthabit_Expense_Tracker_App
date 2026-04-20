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
Communicates with four Spring Boot microservices via REST APIs. 
👉 **[Backend Repository on GitHub](https://github.com/SakshamNarvar/Arthabit-Backend)** — The backend is currently hosted on AWS, but you can also clone and run the services locally for emulator testing.

- **Auth Service:** Login, signup, token validation/refresh.
- **User Service:** User profile retrieval.
- **Expense Service:** Expense CRUD operations.
- **DS Service:** SMS parsing (`/v1/ds/message`) to auto-extract expenses.

*Configure the base URLs in `ApiConfig.kt`. By default, it hits `arthabit-api.sakshamnarvar.tech` via an API Gateway. Change `HOST` to `10.0.2.2` with appropriate ports to target a local emulator or your physical device connected over LAN.*

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
