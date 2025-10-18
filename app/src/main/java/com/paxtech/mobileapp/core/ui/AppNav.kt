package com.paxtech.mobileapp.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.paxtech.mobileapp.features.authentication.presentation.splash.SplashScreen
import com.paxtech.mobileapp.features.authentication.presentation.onboarding.OnboardingScreen1
import com.paxtech.mobileapp.features.authentication.presentation.onboarding.OnboardingScreen2
import com.paxtech.mobileapp.features.authentication.presentation.onboarding.OnboardingScreen3
import com.paxtech.mobileapp.features.authentication.presentation.login.LoginScreen
import com.paxtech.mobileapp.features.authentication.presentation.register.RegisterScreen
import com.paxtech.mobileapp.features.authentication.presentation.register.RegisterType
import com.paxtech.mobileapp.features.authentication.presentation.register.SuccessClientScreen
import com.paxtech.mobileapp.features.authentication.presentation.register.SuccessBusinessScreen
import com.paxtech.mobileapp.features.clientDashboard.presentation.salondetail.SalonDetailRoute
import com.paxtech.mobileapp.features.clientDashboard.presentation.professionalselection.ProfessionalSelectionScreen
import com.paxtech.mobileapp.features.clientDashboard.presentation.timeselection.TimeSelectionScreen
import com.paxtech.mobileapp.features.clientDashboard.presentation.confirmation.ConfirmationScreen
import com.paxtech.mobileapp.features.clientDashboard.presentation.confirmation.ReservationConfirmedScreen
import com.paxtech.mobileapp.features.clientDashboard.presentation.details.ServiceUi
import com.paxtech.mobileapp.features.clientDashboard.presentation.shared.ReservationData
import com.paxtech.mobileapp.features.clientDashboard.presentation.shared.ServiceData

@Preview
@Composable
fun AppNav(){
    val navController = rememberNavController()

    val reservationData = remember { mutableStateOf<ReservationData?>(null) }

    NavHost(navController, startDestination = Route.Splash.route){

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
                onNextClick = {
                    navController.navigate(Route.Onboarding2.route)
                },
                onSkipClick = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.Onboarding2.route) {
            OnboardingScreen2(
                onNextClick = {
                    navController.navigate(Route.Onboarding3.route)
                },
                onSkipClick = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Splash.route) { inclusive = true }
                    }
                }
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
                }
            )
        }

        composable(Route.Login.route) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Route.Home.route) {
                        popUpTo(Route.Splash.route) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Route.Register.route)
                }
            )
        }

        composable(Route.Register.route) {
            RegisterScreen(
                onRegisterClick = { registerType ->
                    when (registerType) {
                        RegisterType.CLIENT -> {
                            navController.navigate(Route.SuccessClient.route)
                        }
                        RegisterType.BUSINESS -> {
                            navController.navigate(Route.SuccessBusiness.route)
                        }
                    }
                },
                onLoginClick = {
                    navController.popBackStack()
                }
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

        composable(Route.Home.route){
            Main(
                onClick = {id ->
                    navController.navigate("${Route.SalonDetails.route}/$id")
                }
            )
        }

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
                onReserveService = { service, salonName, salonAddress, salonRating ->

                    reservationData.value = ReservationData(
                        salonId = salonId,
                        salonName = salonName,
                        salonAddress = salonAddress,
                        salonRating = salonRating,
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

        composable(
            route = Route.ProfessionalSelection.routeWithArgument,
            arguments = listOf(navArgument(Route.ProfessionalSelection.argument) {
                type = NavType.StringType
            })
        ) { backStack ->
            val currentData = reservationData.value
            if (currentData == null) {
                navController.popBackStack()
                return@composable
            }

            ProfessionalSelectionScreen(
                service = ServiceUi(
                    id = currentData.service.id,
                    title = currentData.service.title,
                    subtitle = currentData.service.subtitle,
                    price = currentData.service.price,
                    durationMins = currentData.service.durationMins
                ),
                onBack = {
                    navController.popBackStack()
                },
                onContinue = { selectedProfessional ->
                    reservationData.value = currentData.copy(
                        selectedProfessional = selectedProfessional
                    )

                    navController.navigate("${Route.TimeSelection.route}/${currentData.service.id}") {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Route.TimeSelection.routeWithArgument,
            arguments = listOf(navArgument(Route.TimeSelection.argument) {
                type = NavType.StringType
            })
        ) { backStack ->
            val currentData = reservationData.value
            if (currentData == null) {
                navController.popBackStack()
                return@composable
            }

            TimeSelectionScreen(
                serviceName = currentData.service.title,
                servicePrice = currentData.service.price,
                serviceDuration = currentData.service.durationMins,
                selectedProfessional = currentData.selectedProfessional,
                salonName = currentData.salonName,
                salonAddress = currentData.salonAddress,
                onBack = {
                    navController.popBackStack()
                },
                onContinue = { selectedDate, selectedTime, formattedDate, formattedTime ->

                    reservationData.value = currentData.copy(
                        selectedDate = selectedDate,
                        selectedTime = selectedTime,
                        formattedDate = formattedDate,
                        formattedTime = formattedTime
                    )

                    navController.navigate("${Route.Confirmation.route}/${currentData.service.id}") {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Route.Confirmation.routeWithArgument,
            arguments = listOf(navArgument(Route.Confirmation.argument) {
                type = NavType.StringType
            })
        ) { backStack ->
            val currentData = reservationData.value
            if (currentData == null) {
                navController.popBackStack()
                return@composable
            }

            ConfirmationScreen(
                reservationDetails = com.paxtech.mobileapp.features.clientDashboard.presentation.confirmation.ReservationDetails(
                    salonName = currentData.salonName,
                    rating = currentData.salonRating,
                    address = currentData.salonAddress,
                    serviceName = currentData.service.title,
                    date = currentData.formattedDate,
                    time = currentData.formattedTime,
                    duration = currentData.service.durationMins,
                    professional = currentData.selectedProfessional,
                    totalPrice = currentData.service.price
                ),
                onBack = {
                    navController.popBackStack()
                },
                onConfirm = {
                    println("✅ RESERVA CONFIRMADA CON DATOS REALES:")
                    println("✅ Salón: ${currentData.salonName}")
                    println("✅ Dirección: ${currentData.salonAddress}")
                    println("✅ Servicio: ${currentData.service.title}")
                    println("✅ Precio: ${currentData.service.price}")
                    println("✅ Duración: ${currentData.service.durationMins} min")
                    println("✅ Profesional: ${currentData.selectedProfessional}")
                    println("✅ Fecha: ${currentData.formattedDate}")
                    println("✅ Hora: ${currentData.formattedTime}")


                    navController.navigate(Route.ReservationConfirmed.route) {

                        popUpTo(Route.Home.route) { inclusive = false }
                    }
                }
            )
        }

        composable(Route.ReservationConfirmed.route) {
            val currentData = reservationData.value
            if (currentData != null) {
                ReservationConfirmedScreen(
                    reservationData = currentData,
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

sealed class Route(val route: String) {

    object Splash: Route("splash")
    object Onboarding1: Route("onboarding1")
    object Onboarding2: Route("onboarding2")
    object Onboarding3: Route("onboarding3")
    object Login: Route("login")
    object Register: Route("register")
    object SuccessClient: Route("success_client")
    object SuccessBusiness: Route("success_business")

    object Home : Route("home")
    object Cart : Route("cart")
    object Profile : Route("profile")
    object Services : Route("services")

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