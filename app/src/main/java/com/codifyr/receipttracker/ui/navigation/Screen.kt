package com.codifyr.receipttracker.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object AddReceipt : Screen("add_receipt")
    data object Camera : Screen("camera")
    data object Detail : Screen("detail/{receiptId}") {
        fun createRoute(receiptId: String): String {
            return "detail/$receiptId"
        }
    }
}