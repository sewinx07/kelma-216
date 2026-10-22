package com.example.kelma216.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kelma216.domain.GameConfig
import com.example.kelma216.ui.screens.completion.CompletionScreen
import com.example.kelma216.ui.screens.governorate.GovernorateScreen
import com.example.kelma216.ui.screens.home.HomeScreen
import com.example.kelma216.ui.screens.map.MapScreen
import com.example.kelma216.ui.screens.profile.ProfileScreen
import com.example.kelma216.ui.screens.puzzle.PuzzleScreen
import com.example.kelma216.ui.screens.settings.SettingsScreen
import com.example.kelma216.viewmodel.GameViewModel

/**
 * Root composable: sets up Navigation Compose and a single shared [GameViewModel] so
 * progress stays consistent across every screen.
 */
@Composable
fun KelmaApp() {
    val navController = rememberNavController()
    val viewModel: GameViewModel = viewModel()

    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(
                viewModel = viewModel,
                onPlay = { navController.navigate(Routes.MAP) },
                onContinue = { govId -> navController.navigate(Routes.governorate(govId)) },
                onProfile = { navController.navigate(Routes.PROFILE) },
                onSettings = { navController.navigate(Routes.SETTINGS) },
            )
        }

        composable(Routes.MAP) {
            MapScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onOpenGovernorate = { govId -> navController.navigate(Routes.governorate(govId)) },
            )
        }

        composable(
            route = Routes.GOVERNORATE,
            arguments = listOf(navArgument("govId") { type = NavType.StringType }),
        ) { entry ->
            val govId = entry.arguments?.getString("govId") ?: GameConfig.FIRST_GOVERNORATE_ID
            GovernorateScreen(
                viewModel = viewModel,
                govId = govId,
                onBack = { navController.popBackStack() },
                onStartPuzzle = { puzzleId -> navController.navigate(Routes.puzzle(puzzleId)) },
                onCompletion = { id -> navController.navigate(Routes.completion(id)) },
            )
        }

        composable(
            route = Routes.PUZZLE,
            arguments = listOf(navArgument("puzzleId") { type = NavType.StringType }),
        ) { entry ->
            val puzzleId = entry.arguments?.getString("puzzleId").orEmpty()
            PuzzleScreen(
                viewModel = viewModel,
                puzzleId = puzzleId,
                onBack = { navController.popBackStack() },
                onNextPuzzle = { nextId ->
                    // Replace current puzzle so back never walks through solved puzzles.
                    navController.navigate(Routes.puzzle(nextId)) {
                        popUpTo(Routes.PUZZLE) { inclusive = true }
                    }
                },
                onCompletion = { govId ->
                    navController.navigate(Routes.completion(govId)) {
                        popUpTo(Routes.PUZZLE) { inclusive = true }
                    }
                },
            )
        }

        composable(
            route = Routes.COMPLETION,
            arguments = listOf(navArgument("govId") { type = NavType.StringType }),
        ) { entry ->
            val govId = entry.arguments?.getString("govId") ?: GameConfig.FIRST_GOVERNORATE_ID
            CompletionScreen(
                viewModel = viewModel,
                govId = govId,
                onBackToMap = {
                    navController.navigate(Routes.MAP) {
                        popUpTo(Routes.MAP) { inclusive = true }
                    }
                },
                onOpenGovernorate = { id ->
                    navController.navigate(Routes.governorate(id)) {
                        popUpTo(Routes.MAP)
                    }
                },
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
