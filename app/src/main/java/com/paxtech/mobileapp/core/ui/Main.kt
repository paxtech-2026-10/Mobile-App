package com.paxtech.mobileapp.core.ui

import android.net.http.SslCertificate.restoreState
import android.net.http.SslCertificate.saveState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import com.paxtech.mobileapp.ui.theme.PrimaryPurple
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
        Route.Location to Icons.Default.LocationOn,
        Route.Booking to Icons.Default.CalendarToday,
        Route.Message to Icons.Default.Message,
        Route.Profile to Icons.Default.Person
    )

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                        spotColor = Color.Black.copy(alpha = 0.1f),
                        ambientColor = Color.Black.copy(alpha = 0.05f)
                    )
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val currentRoute = tabNav.currentBackStackEntryAsState().value?.destination?.route
                    tabs.forEach { (route, icon) ->
                        NavigationBarItem(
                            selected = currentRoute == route.route,
                            icon = { 
                                Icon(
                                    icon, 
                                    contentDescription = route.route,
                                    tint = if (currentRoute == route.route) PrimaryPurple else Color(0xFF6B7280)
                                ) 
                            },
                            label = { 
                                Text(
                                    text = route.route.replaceFirstChar { it.titlecase() },
                                    color = if (currentRoute == route.route) PrimaryPurple else Color(0xFF6B7280)
                                ) 
                            },
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
        }
    ) { innerPadding ->
        // THIS renders the content for each tab
        NavHost(
            navController = tabNav,
            startDestination = Route.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Route.Home.route)     { Home(onSalonClick = onClick)  }
            composable(Route.Location.route) { LocationPlaceholder() }
            composable(Route.Booking.route)  { BookingPlaceholder() }
            composable(Route.Message.route)  { MessagePlaceholder() }
            composable(Route.Profile.route)  { ProfileNav() }
        }
    }
}


@Composable
fun LocationPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Location",
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Location screen coming soon...",
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun BookingPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Booking",
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Booking screen coming soon...",
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun MessagePlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Messages",
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Messages screen coming soon...",
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview(){
    Main {}
}