package com.codifyr.receipttracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.codifyr.receipttracker.ui.add_receipt.AddReceiptScreen
import com.codifyr.receipttracker.ui.camera.CameraScreen
import com.codifyr.receipttracker.ui.home.HomeScreen
import com.codifyr.receipttracker.ui.receipt_details.DetailScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {

        // الشاشة الرئيسية
        composable(route = Screen.Home.route) {
            HomeScreen(
                onAddClick = {
                    navController.navigate(Screen.AddReceipt.route)
                },
                onReceiptClick = { receiptId ->
                    navController.navigate(
                        Screen.Detail.createRoute(receiptId)
                    )
                }
            )
        }

        // شاشة إضافة فاتورة
        composable(route = Screen.AddReceipt.route) { backStackEntry ->
            // استقبال نتيجة OCR من الكاميرا
            val scannedText = backStackEntry
                .savedStateHandle
                .get<String>("scanned_text")

            AddReceiptScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onOpenCamera = {
                    navController.navigate(Screen.Camera.route)
                },
                scannedText = scannedText
            )
        }

        // شاشة الكاميرا
        composable(route = Screen.Camera.route) {
            CameraScreen(
                onNavigateBack = { scannedText ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("scanned_text", scannedText)
                    navController.popBackStack()
                }
            )
        }

        // شاشة التفاصيل
        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("receiptId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val receiptId = backStackEntry.arguments
                ?.getString("receiptId") ?: ""

            DetailScreen(
                receiptId = receiptId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}