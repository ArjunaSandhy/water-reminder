package com.waterreminder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.waterreminder.ui.add.AddWaterScreen
import com.waterreminder.ui.history.HistoryDetailScreen
import com.waterreminder.ui.history.HistoryScreen
import com.waterreminder.ui.home.HomeScreen
import com.waterreminder.ui.navigation.BottomNavItem
import com.waterreminder.ui.navigation.Screen
import com.waterreminder.ui.reminder.ReminderScreen
import com.waterreminder.ui.settings.SettingsScreen
import com.waterreminder.ui.splash.SplashScreen
import com.waterreminder.ui.success.SuccessScreen
import com.waterreminder.ui.theme.WaterReminderTheme
import com.waterreminder.worker.MidnightResetWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Schedule midnight reset worker
        MidnightResetWorker.scheduleNextMidnight(this)
        
        setContent {
            WaterReminderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WaterReminderApp()
                }
            }
        }
    }
}

@Composable
fun WaterReminderApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // Routes where bottom nav should be shown
    val bottomNavRoutes = listOf(
        Screen.Home.route,
        Screen.History.route,
        Screen.Reminder.route,
        Screen.Settings.route
    )
    
    val showBottomNav = currentDestination?.route in bottomNavRoutes
    
    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomNav,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    BottomNavItem.items.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(item.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Splash Screen
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }
            
            // Home Screen
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToAddWater = {
                        navController.navigate(Screen.AddWater.route)
                    }
                )
            }
            
            // Add Water Screen
            composable(Screen.AddWater.route) {
                AddWaterScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToSuccess = { amount ->
                        navController.navigate(Screen.Success.createRoute(amount)) {
                            popUpTo(Screen.AddWater.route) { inclusive = true }
                        }
                    }
                )
            }
            
            // Success Screen
            composable(
                route = Screen.Success.route,
                arguments = listOf(
                    navArgument("amountAdded") { type = NavType.IntType }
                )
            ) {
                SuccessScreen(
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }
            
            // History Screen
            composable(Screen.History.route) {
                HistoryScreen(
                    onNavigateToDetail = { date ->
                        navController.navigate(Screen.HistoryDetail.createRoute(date))
                    },
                    onNavigateToAddWater = {
                        navController.navigate(Screen.AddWater.route)
                    }
                )
            }
            
            // History Detail Screen
            composable(
                route = Screen.HistoryDetail.route,
                arguments = listOf(
                    navArgument("date") { type = NavType.StringType }
                )
            ) {
                HistoryDetailScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Reminder Screen
            composable(Screen.Reminder.route) {
                ReminderScreen()
            }
            
            // Settings Screen
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
