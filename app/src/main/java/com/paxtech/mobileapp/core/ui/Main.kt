package com.paxtech.mobileapp.core.ui

import android.net.http.SslCertificate.restoreState
import android.net.http.SslCertificate.saveState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.paxtech.mobileapp.features.clientDashboard.presentation.home.Home
import com.paxtech.mobileapp.features.clientDashboard.presentation.home.SalonDebugScreen
import com.paxtech.mobileapp.features.clientDashboard.presentation.home.ApiDebugScreen
import com.paxtech.mobileapp.features.profile.presentation.ProfileNav
import com.paxtech.mobileapp.features.services.presentation.SearchServiceView

data class NavigationItem(
    val icon: ImageVector,
    val route: String
)


@Composable
fun Main(onClick: (Int) -> Unit) {

    val tabNav = rememberNavController()

    // map each tab to its route + icon (use your sealed routes)
    val tabs = listOf(
        Route.Home to Icons.Default.Home,
        Route.Services to Icons.Default.Search,      // your magnifier icon
        Route.Cart to Icons.Default.CalendarToday,    // your calendar icon
        Route.Profile to Icons.Default.Person
    )

    Scaffold(
        bottomBar = {
            BottomAppBar {
                val currentRoute = tabNav.currentBackStackEntryAsState().value?.destination?.route
                tabs.forEach { (route, icon) ->
                    NavigationBarItem(
                        selected = currentRoute == route.route,
                        icon = { Icon(icon, contentDescription = route.route) },
                        label = { Text(route.route.replaceFirstChar { it.titlecase() }) },
                        onClick = {
                            tabNav.navigate(route.route) {
                                popUpTo(tabNav.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // THIS renders the content for each tab
        NavHost(
            navController = tabNav,
            startDestination = Route.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Route.Home.route)     { Home(onSalonClick = onClick)  }
            composable(Route.Services.route) { SearchServiceView() }
            composable(Route.Cart.route)     { ApiDebugScreen() }
            composable(Route.Profile.route)  { ProfileNav() }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainPreview(){
    Main {}
}