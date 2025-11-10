package com.paxtech.mobileapp.core.ui

import android.net.http.SslCertificate.restoreState
import android.net.http.SslCertificate.saveState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
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
    val route: String,
    val label: String
)


@Composable
fun Main(onClick: (Int) -> Unit) {

    val tabNav = rememberNavController()

    // map each tab to its route + icon (use your sealed routes)
    val tabs = listOf(
        NavigationItem(Icons.Default.Home, Route.Home.route, "Inicio"),
        NavigationItem(Icons.Default.Search, Route.Services.route, "Buscador"),
        NavigationItem(Icons.Default.CalendarToday, Route.Booking.route, "Reservación"),
        NavigationItem(Icons.Default.Person, Route.Profile.route, "Perfil")
    )

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(24.dp),
                        spotColor = Color.Black.copy(alpha = 0.15f),
                        ambientColor = Color.Black.copy(alpha = 0.1f)
                    )
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val currentRoute = tabNav.currentBackStackEntryAsState().value?.destination?.route
                    tabs.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            icon = { 
                                Icon(
                                    item.icon, 
                                    contentDescription = item.label,
                                    tint = if (isSelected) PrimaryPurple else Color(0xFF6B7280),
                                    modifier = Modifier.size(24.dp)
                                ) 
                            },
                            label = { 
                                Text(
                                    text = item.label,
                                    color = if (isSelected) PrimaryPurple else Color(0xFF6B7280),
                                    style = MaterialTheme.typography.labelSmall
                                ) 
                            },
                            colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryPurple,
                                selectedTextColor = PrimaryPurple,
                                indicatorColor = Color.Transparent,
                                unselectedIconColor = Color(0xFF6B7280),
                                unselectedTextColor = Color(0xFF6B7280)
                            ),
                            onClick = {
                                tabNav.navigate(item.route) {
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
            composable(Route.Services.route) { SearchServiceView() }
            composable(Route.Booking.route)  { BookingPlaceholder() }
            composable(Route.Profile.route)  { ProfileNav() }
        }
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
            text = "Reservaciones",
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Pantalla de reservaciones próximamente...",
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview(){
    Main {}
}