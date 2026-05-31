package com.waterreminder.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object AddWater : Screen("add_water")
    object Success : Screen("success/{amountAdded}") {
        fun createRoute(amountAdded: Int) = "success/$amountAdded"
    }
    object History : Screen("history")
    object HistoryDetail : Screen("history_detail/{date}") {
        fun createRoute(date: String) = "history_detail/$date"
    }
    object Reminder : Screen("reminder")
    object Settings : Screen("settings")
}
