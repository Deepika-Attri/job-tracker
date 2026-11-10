package com.example.jobtracker.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jobtracker.ui.detail.JobDetailScreen
import com.example.jobtracker.ui.discover.DiscoverScreen
import com.example.jobtracker.ui.edit.EditJobScreen
import com.example.jobtracker.ui.list.JobListScreen
import com.example.jobtracker.ui.settings.SettingsScreen
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

// ---------- Routes (each screen is a Kotlin class) ----------
@Serializable data object JobList
@Serializable data object Discover
@Serializable data object Settings
@Serializable data class JobDetail(val jobId: Int)
@Serializable data class EditJob(val jobId: Int)      // jobId = -1 means "add new"

// ---------- Bottom bar items ----------
private data class TopLevelItem(
    val label: String,
    val route: Any,
    val routeClass: KClass<*>,
    val icon: ImageVector
)

private val topLevelItems = listOf(
    TopLevelItem("Applications", JobList, JobList::class, Icons.AutoMirrored.Filled.List),
    TopLevelItem("Discover", Discover, Discover::class, Icons.Default.Search)
)

private fun NavDestination?.isInHierarchy(route: KClass<*>): Boolean =
    this?.hierarchy?.any { it.hasRoute(route) } == true

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    // Show the bottom bar only on the two main screens
    val showBottomBar = topLevelItems.any { currentDestination.isInHierarchy(it.routeClass) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),   // each screen handles its own insets
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    topLevelItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentDestination.isInHierarchy(item.routeClass),
                            onClick = {
                                navController.navigate(item.route) {
                                    // Keep one copy of each tab and remember its state
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = null) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = JobList,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<JobList> {
                JobListScreen(
                    onAddClick = { navController.navigate(EditJob(jobId = -1)) },
                    onJobClick = { id -> navController.navigate(JobDetail(id)) },
                    onSettingsClick = { navController.navigate(Settings) }
                )
            }

            composable<Discover> {
                DiscoverScreen()
            }

            composable<JobDetail> {
                JobDetailScreen(
                    onBack = { navController.popBackStack() },
                    onEdit = { id -> navController.navigate(EditJob(id)) }
                )
            }

            composable<EditJob> {
                EditJobScreen(onDone = { navController.popBackStack() })
            }

            composable<Settings> {
                SettingsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}