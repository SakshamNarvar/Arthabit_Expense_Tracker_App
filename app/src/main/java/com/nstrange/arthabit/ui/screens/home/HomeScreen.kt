package com.nstrange.arthabit.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nstrange.arthabit.ui.components.AddExpenseBottomSheet
import com.nstrange.arthabit.ui.components.ExpenseItem
import com.nstrange.arthabit.ui.components.Heading
import com.nstrange.arthabit.ui.theme.DarkBackground
import com.nstrange.arthabit.ui.theme.DarkTextSecondary
import com.nstrange.arthabit.ui.theme.Primary
import com.nstrange.arthabit.ui.theme.StatusError

@Composable
fun HomeScreen(
    onNavigateToProfile: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.syncSms()
        }
    }

    val onSyncClick = {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            viewModel.syncSms()
        } else {
            permissionLauncher.launch(Manifest.permission.READ_SMS)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::showAddExpenseSheet,
                containerColor = Primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Expense"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .statusBarsPadding()
        ) {
            // ── Nav Bar ──
            NavBar(
                onAvatarClick = onNavigateToProfile,
                onSyncClick = onSyncClick,
                isSyncing = uiState.isSyncingSms
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Spends Section ──
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(DarkBackground)
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Heading(text = "Your Recent Spends")

                Spacer(modifier = Modifier.height(16.dp))

                when {
                    uiState.isLoadingExpenses -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = Primary)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Loading expenses...",
                                    color = DarkTextSecondary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    uiState.expenseError != null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Error: ${uiState.expenseError}",
                                color = StatusError,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    uiState.expenses.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No expenses found.\nStart adding some!",
                                color = DarkTextSecondary,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(uiState.expenses, key = { it.key }) { expense ->
                                ExpenseItem(
                                    expense = expense,
                                    modifier = Modifier.clickable {
                                        viewModel.showEditExpenseSheet(expense)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ── Add/Edit Expense Bottom Sheet ──
    if (uiState.showAddExpenseSheet) {
        val isEditing = uiState.expenseToEdit != null
        AddExpenseBottomSheet(
            expenseToEdit = uiState.expenseToEdit,
            isLoading = if (isEditing) uiState.isUpdatingExpense else uiState.isAddingExpense,
            onDismiss = viewModel::hideAddExpenseSheet,
            onSaveExpense = { amount, merchant, currency, notes, category, fundSource ->
                if (isEditing) {
                    val externalId = uiState.expenseToEdit?.externalId
                    if (externalId != null) {
                        viewModel.updateExpense(
                            externalId = externalId,
                            amount = amount,
                            merchant = merchant,
                            currency = currency,
                            notes = notes,
                            category = category,
                            fundSource = fundSource
                        )
                    }
                } else {
                    viewModel.addExpense(amount, merchant, currency, notes, category, fundSource)
                }
            }
        )
    }
}

@Composable
private fun NavBar(
    onAvatarClick: () -> Unit,
    onSyncClick: () -> Unit,
    isSyncing: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Logo",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Text(
            text = "Arthabit",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isSyncing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp).padding(end = 8.dp),
                    color = Primary,
                    strokeWidth = 2.dp
                )
            } else {
                IconButton(onClick = onSyncClick) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Sync SMS",
                        tint = Primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            AsyncImage(
                model = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=80&h=80&fit=crop",
                contentDescription = "Profile",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onAvatarClick),
                contentScale = ContentScale.Crop
            )
        }
    }
}
