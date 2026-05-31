package com.waterreminder.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.waterreminder.ui.add.AddWaterScreen
import com.waterreminder.ui.history.HistoryDetailScreen
import com.waterreminder.ui.history.HistoryScreen
import com.waterreminder.ui.home.HomeScreen
import com.waterreminder.ui.reminder.ReminderScreen
import com.waterreminder.ui.settings.SettingsScreen
import com.waterreminder.ui.splash.SplashScreen
import com.waterreminder.ui.success.SuccessScreen

object Routes {
    const val SPLASH = "splash"
    const val HOME = "home"
    const val ADD_WATER = "add_water"
    const val SUCCESS = "success/{amountAdded}"
    const val HISTORY = "history"
    const val HISTORY_DETAIL = "history_detail/{date}"
    const val REMINDER = "reminder"
    const val SETTINGS = "settings"
    
    fun success(amountAdded: Int) = "success/$amountAdded"
    fun historyDetail(date: String) = "history_detail/$date"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Routes.SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Splash Screen
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        
        // Home Screen
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToAddWater = {
                    navController.navigate(Routes.ADD_WATER)
                },
                onNavigateToReminder = {
                    navController.navigate(Routes.REMINDER)
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTINGS)
                }
            )
        }
        
        // Add Water Screen
        composable(Routes.ADD_WATER) {
            AddWaterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToSuccess = { amount ->
                    navController.navigate(Routes.success(amount)) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }
        
        // Success Screen
        composable(
            route = Routes.SUCCESS,
            arguments = listOf(
                navArgument("amountAdded") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val amountAdded = backStackEntry.arguments?.getInt("amountAdded") ?: 0
            SuccessScreen(
                amountAdded = amountAdded,
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }
        
        // History Screen
        composable(Routes.HISTORY) {
            HistoryScreen(
                onNavigateToDetail = { date ->
                    navController.navigate(Routes.historyDetail(date))
                },
                onNavigateToAddWater = {
                    navController.navigate(Routes.ADD_WATER)
                }
            )
        }
        
        // History Detail Screen
        composable(
            route = Routes.HISTORY_DETAIL,
            arguments = listOf(
                navArgument("date") { type = NavType.StringType }
            )
        ) {
            HistoryDetailScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Reminder Screen
        composable(Routes.REMINDER) {
            ReminderScreen()
        }
        
        // Settings Screen
        composable(Routes.SETTINGS) {
            SettingsScreen()
        }
    }
}
