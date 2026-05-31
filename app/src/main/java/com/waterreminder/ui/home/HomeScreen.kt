package com.waterreminder.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.waterreminder.ui.components.WaterCircularProgress
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAddWater: () -> Unit,
    onNavigateToReminder: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Show error snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                actionLabel = "Coba Lagi"
            ).let { result ->
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.retry()
                }
            }
            viewModel.clearError()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💧")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Water Reminder")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToReminder) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Pengingat"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Greeting
                val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                val motivationalText = getMotivationalText(dayOfWeek)
                
                Text(
                    text = "Halo, 👋",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = motivationalText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Target Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Target Harian",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "${uiState.targetMl} ml",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        TextButton(onClick = onNavigateToSettings) {
                            Text("Ubah")
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Circular Progress
                WaterCircularProgress(
                    progress = uiState.percentage,
                    currentMl = uiState.currentMl,
                    targetMl = uiState.targetMl,
                    size = 200.dp,
                    strokeWidth = 16.dp
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Remaining Card
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (uiState.isTargetReached) "Target Tercapai!" else "Sisa Target",
                                style = MaterialTheme.typography.titleMedium
                            )
                            if (uiState.isTargetReached) {
                                Text(
                                    text = "✅ Luar biasa!",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(
                                    text = "${uiState.remainingMl} ml",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = "🥤",
                            style = MaterialTheme.typography.displayMedium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Empty state message
                if (uiState.currentMl == 0) {
                    Text(
                        text = "Yuk mulai minum air hari ini!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Add Water Button
                Button(
                    onClick = onNavigateToAddWater,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "+ Tambah Minum",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

private fun getMotivationalText(dayOfWeek: Int): String {
    return when (dayOfWeek) {
        Calendar.SUNDAY -> "Selamat hari Minggu! Tetap terhidrasi ya"
        Calendar.MONDAY -> "Semangat memulai minggu dengan minum air yang cukup!"
        Calendar.TUESDAY -> "Hari Selasa yang produktif dimulai dengan air"
        Calendar.WEDNESDAY -> "Sudah setengah minggu, jangan lupa minum!"
        Calendar.THURSDAY -> "Kamis penuh semangat dengan tubuh yang terhidrasi"
        Calendar.FRIDAY -> "Jumat ceria dengan air yang cukup!"
        Calendar.SATURDAY -> "Sabtu santai, tetap jaga kesehatan!"
        else -> "Jangan lupa minum air, jaga tubuh tetap sehat"
    }
}
