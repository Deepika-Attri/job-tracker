package com.example.jobtracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.jobtracker.ui.detail.JobDetailScreen
import com.example.jobtracker.ui.edit.EditJobScreen
import com.example.jobtracker.ui.list.JobListScreen
import com.example.jobtracker.ui.settings.SettingsScreen

// All route names in one place
object Routes {
    const val LIST = "list"
    const val DETAIL = "detail/{jobId}"
    const val EDIT = "edit/{jobId}"
    const val SETTINGS = "settings"

    fun detail(id: Int) = "detail/$id"
    fun edit(id: Int) = "edit/$id"   // use -1 for a new job
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val jobIdArg = listOf(navArgument("jobId") { type = NavType.IntType })

    NavHost(navController = navController, startDestination = Routes.LIST) {

        composable(Routes.DETAIL, arguments = jobIdArg) {
            JobDetailScreen(
                onBack = { navController.popBackStack() },
                onEdit = { id -> navController.navigate(Routes.edit(id)) }
            )
        }

        composable(Routes.EDIT, arguments = jobIdArg) {
            EditJobScreen(onDone = { navController.popBackStack() })
        }

        composable(Routes.LIST) {
            JobListScreen(
                onAddClick = { navController.navigate(Routes.edit(-1)) },
                onJobClick = { id -> navController.navigate(Routes.detail(id)) },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) }   // new
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}