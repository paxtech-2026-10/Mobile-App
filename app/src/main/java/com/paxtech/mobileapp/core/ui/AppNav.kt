package com.paxtech.mobileapp.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.paxtech.mobileapp.features.authentication.presentation.login.LoginScreen
import com.paxtech.mobileapp.features.authentication.presentation.onboarding.OnboardingScreen1
import com.paxtech.mobileapp.features.authentication.presentation.onboarding.OnboardingScreen2
import com.paxtech.mobileapp.features.authentication.presentation.onboarding.OnboardingScreen3
import com.paxtech.mobileapp.features.authentication.presentation.register.RegisterScreen
import com.paxtech.mobileapp.features.authentication.presentation.register.RegisterType
import com.paxtech.mobileapp.features.authentication.presentation.register.SuccessBusinessScreen
import com.paxtech.mobileapp.features.authentication.presentation.register.SuccessClientScreen
import com.paxtech.mobileapp.features.authentication.presentation.splash.SplashScreen
import com.paxtech.mobileapp.features.clientDashboard.presentation.confirmation.ConfirmationScreen
import com.paxtech.mobileapp.features.clientDashboard.presentation.confirmation.ReservationConfirmedScreen
import com.paxtech.mobileapp.features.clientDashboard.presentation.details.ServiceUi
import com.paxtech.mobileapp.features.clientDashboard.presentation.professionalselection.ProfessionalSelectionScreen
import com.paxtech.mobileapp.features.clientDashboard.presentation.salondetail.SalonDetailRoute
import com.paxtech.mobileapp.features.clientDashboard.presentation.shared.ReservationData
import com.paxtech.mobileapp.features.clientDashboard.presentation.shared.ServiceData
import com.paxtech.mobileapp.features.clientDashboard.presentation.timeselection.TimeSelectionScreen
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.platform.LocalContext

@Preview
@Composable
fun AppNav() {
    val navController = rememberNavController()
    val reservationData = remember { mutableStateOf<ReservationData?>(null) }
    val context = LocalContext.current
    val authPrefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    NavHost(navController, startDestination = Route.Splash.route) {

        // Splash / Onboarding / Auth
        composable(Route.Splash.route) {
            SplashScreen(
                onNavigateToWelcome = {
                    navController.navigate(Route.Onboarding1.route) {
                        popUpTo(Route.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.Onboarding1.route) {
            OnboardingScreen1(
                onNextClick = { navController.navigate(Route.Onboarding2.route) },
                onSkipClick = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Splash.route) { inclusive = true }
                    }
                },
                onPage1Click = { /* Ya estamos en la página 1 */ },
                onPage2Click = { navController.navigate(Route.Onboarding2.route) },
                onPage3Click = { navController.navigate(Route.Onboarding3.route) }
            )
        }

        composable(Route.Onboarding2.route) {
            OnboardingScreen2(
                onNextClick = { navController.navigate(Route.Onboarding3.route) },
                onSkipClick = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Splash.route) { inclusive = true }
                    }
                },
                onPage1Click = { navController.navigate(Route.Onboarding1.route) },
                onPage2Click = { /* Ya estamos en la página 2 */ },
                onPage3Click = { navController.navigate(Route.Onboarding3.route) }
            )
        }

        composable(Route.Onboarding3.route) {
            OnboardingScreen3(
                onStartClick = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Splash.route) { inclusive = true }
                    }
                },
                onSkipClick = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Splash.route) { inclusive = true }
                    }
                },
                onPage1Click = { navController.navigate(Route.Onboarding1.route) },
                onPage2Click = { navController.navigate(Route.Onboarding2.route) },
                onPage3Click = { /* Ya estamos en la página 3 */ }
            )
        }

        composable(Route.Login.route) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Route.Home.route) {
                        popUpTo(Route.Splash.route) { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate(Route.Register.route) }
            )
        }

        composable(Route.Register.route) {
            RegisterScreen(
                onRegisterClick = { registerType ->
                    when (registerType) {
                        RegisterType.CLIENT -> navController.navigate(Route.SuccessClient.route)
                        RegisterType.BUSINESS -> navController.navigate(Route.SuccessBusiness.route)
                    }
                },
                onLoginClick = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Route.SuccessClient.route) {
            SuccessClientScreen(
                onStartNowClick = {
                    navController.navigate(Route.Home.route) {
                        popUpTo(Route.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.SuccessBusiness.route) {
            SuccessBusinessScreen(
                onStartNowClick = {
                    navController.navigate(Route.Home.route) {
                        popUpTo(Route.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Home
        composable(Route.Home.route) {
            Main(
                onClick = { id ->
                    navController.navigate("${Route.SalonDetails.route}/$id")
                },
                onLogout = {
                    authPrefs.edit().clear().apply()
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Detalle del salón
        composable(
            route = Route.SalonDetails.routeWithArgument,
            arguments = listOf(navArgument(Route.SalonDetails.argument) {
                type = NavType.IntType
            })
        ) { backStack ->
            val salonId = backStack.arguments?.getInt(Route.SalonDetails.argument) ?: 0

            SalonDetailRoute(
                salonId = salonId,
                onBack = { navController.popBackStack() },
                onReserveService = { service: ServiceUi, salonName: String, salonAddress: String, salonRating: Double, salonImageUrl: String ->
                    // Guardamos TODOS los datos reales, incluida la imagen
                    reservationData.value = ReservationData(
                        salonId = salonId,
                        salonName = salonName,
                        salonAddress = salonAddress,
                        salonRating = salonRating,
                        salonImageUrl = salonImageUrl,
                        clientId = authPrefs.getInt("client_id", 0).toLong(),
                        providerId = salonId.toLong(),
                        service = ServiceData(
                            id = service.id,
                            title = service.title,
                            subtitle = service.subtitle,
                            price = service.price,
                            durationMins = service.durationMins
                        )
                    )
                    navController.navigate("${Route.ProfessionalSelection.route}/${service.id}") {
                        launchSingleTop = true
                    }
                }
            )
        }

        // Selección de profesional
        composable(
            route = Route.ProfessionalSelection.routeWithArgument,
            arguments = listOf(navArgument(Route.ProfessionalSelection.argument) {
                type = NavType.StringType
            })
        ) {
            val current = reservationData.value ?: run {
                navController.popBackStack()
                return@composable
            }

            ProfessionalSelectionScreen(
                service = ServiceUi(
                    id = current.service.id,
                    title = current.service.title,
                    subtitle = current.service.subtitle,
                    price = current.service.price,
                    durationMins = current.service.durationMins
                ),
                providerId = current.salonId.toLong(),
                onBack = { navController.popBackStack() },
                onContinue = { selectedProfessional, workerId ->
                    println("🔍 AppNav: Guardando worker en ReservationData - Nombre: $selectedProfessional, ID: $workerId")
                    reservationData.value = current.copy(
                        selectedProfessional = selectedProfessional,
                        selectedProfessionalId = workerId
                    )
                    println("🔍 AppNav: ReservationData actualizado - selectedProfessionalId: ${reservationData.value?.selectedProfessionalId}")
                    navController.navigate("${Route.TimeSelection.route}/${current.service.id}") {
                        launchSingleTop = true
                    }
                }
            )
        }

        // Selección de hora
        composable(
            route = Route.TimeSelection.routeWithArgument,
            arguments = listOf(navArgument(Route.TimeSelection.argument) {
                type = NavType.StringType
            })
        ) {
            val current = reservationData.value ?: run {
                navController.popBackStack()
                return@composable
            }

            TimeSelectionScreen(
                serviceName = current.service.title,
                servicePrice = current.service.price,
                serviceDuration = current.service.durationMins,
                serviceId = current.service.id.toLongOrNull() ?: 0L,
                selectedProfessional = current.selectedProfessional,
                clientId = authPrefs.getInt("client_id", 0).toLong(),
                providerId = current.salonId.toLong(),
                workerId = current.selectedProfessionalId,
                salonName = current.salonName,
                salonAddress = current.salonAddress,
                onBack = { navController.popBackStack() },
                onContinue = { selectedDate, selectedTime, formattedDate, formattedTime ->
                    reservationData.value = current.copy(
                        selectedDate = selectedDate,
                        selectedTime = selectedTime,
                        formattedDate = formattedDate,
                        formattedTime = formattedTime
                    )
                    navController.navigate("${Route.Confirmation.route}/${current.service.id}") {
                        launchSingleTop = true
                    }
                }
            )
        }

        // Confirmación
        composable(
            route = Route.Confirmation.routeWithArgument,
            arguments = listOf(navArgument(Route.Confirmation.argument) {
                type = NavType.StringType
            })
        ) {
            val current = reservationData.value ?: run {
                navController.popBackStack()
                return@composable
            }

            ConfirmationScreen(
                reservationDetails = com.paxtech.mobileapp.features.clientDashboard.presentation.confirmation.ReservationDetails(
                    salonName = current.salonName,
                    rating = current.salonRating,
                    address = current.salonAddress,
                    serviceName = current.service.title,
                    date = current.formattedDate,
                    time = current.formattedTime,
                    duration = current.service.durationMins,
                    professional = current.selectedProfessional,
                    totalPrice = current.service.price,
                    salonImageUrl = current.salonImageUrl // <-- imagen real en la tarjeta
                ),
                onBack = { navController.popBackStack() },
                onConfirm = {
                    navController.navigate(Route.ReservationConfirmed.route) {
                        popUpTo(Route.Home.route) { inclusive = false }
                    }
                }
            )
        }

        // Pantalla final
        composable(Route.ReservationConfirmed.route) {
            val current = reservationData.value
            if (current != null) {
                ReservationConfirmedScreen(
                    reservationData = current,
                    onBackToHome = {
                        navController.navigate(Route.Home.route) {
                            popUpTo(Route.Home.route) { inclusive = true }
                        }
                    }
                )
            } else {
                navController.navigate(Route.Home.route) {
                    popUpTo(Route.Home.route) { inclusive = true }
                }
            }
        }
    }
}

// -------- Rutas ----------
sealed class Route(val route: String) {
    object Splash : Route("splash")
    object Onboarding1 : Route("onboarding1")
    object Onboarding2 : Route("onboarding2")
    object Onboarding3 : Route("onboarding3")
    object Login : Route("login")
    object Register : Route("register")
    object SuccessClient : Route("success_client")
    object SuccessBusiness : Route("success_business")

    object Home : Route("home")
    object Location : Route("location")
    object Booking : Route("booking")
    object Message : Route("message")
    object Profile : Route("profile")
    object Services : Route("services")
    object Cart : Route("cart")

    object SalonDetails : Route("salon_detail") {
        const val routeWithArgument = "salon_detail/{id}"
        const val argument = "id"
    }

    object ProfessionalSelection : Route("professional_selection") {
        const val routeWithArgument = "professional_selection/{service_id}"
        const val argument = "service_id"
    }

    object TimeSelection : Route("time_selection") {
        const val routeWithArgument = "time_selection/{service_id}"
        const val argument = "service_id"
    }

    object Confirmation : Route("confirmation") {
        const val routeWithArgument = "confirmation/{service_id}"
        const val argument = "service_id"
    }

    object ReservationConfirmed : Route("reservation_confirmed")
}
