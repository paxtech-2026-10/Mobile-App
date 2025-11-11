package com.paxtech.mobileapp.features.profile.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.paxtech.mobileapp.features.profile.presentation.screen.AboutUsScreen
import com.paxtech.mobileapp.features.profile.presentation.screen.ChangePasswordScreen
import com.paxtech.mobileapp.features.profile.presentation.screen.EditProfileScreen
import com.paxtech.mobileapp.features.profile.presentation.screen.FaqScreen
import com.paxtech.mobileapp.features.profile.presentation.screen.FavoriteSalonsScreen
import com.paxtech.mobileapp.features.profile.presentation.screen.MyProfileScreen
import com.paxtech.mobileapp.features.profile.presentation.screen.NotificationsScreen
import com.paxtech.mobileapp.features.profile.presentation.screen.PaymentMethodFormScreen
import com.paxtech.mobileapp.features.profile.presentation.screen.PaymentMethodsScreen
import com.paxtech.mobileapp.features.profile.presentation.screen.ProfileScreen

private sealed class ProfileDestination(val route: String) {
    object Home : ProfileDestination("profile_home")
    object MyProfile : ProfileDestination("profile_my_profile")
    object EditProfile : ProfileDestination("profile_edit_profile")
    object PaymentMethods : ProfileDestination("profile_payment_methods")
    object AddPaymentMethod : ProfileDestination("profile_payment_methods/add")
    object EditPaymentMethod : ProfileDestination("profile_payment_methods/edit/{methodId}") {
        const val METHOD_ID = "methodId"
        fun createRoute(methodId: String) = "profile_payment_methods/edit/$methodId"
    }
    object FavoriteSalons : ProfileDestination("profile_favorite_salons")
    object Notifications : ProfileDestination("profile_notifications")
    object ChangePassword : ProfileDestination("profile_change_password")
    object Faq : ProfileDestination("profile_faq")
    object About : ProfileDestination("profile_about")
}

private data class ProfileNavActions(
    val openMyProfile: () -> Unit,
    val openEditProfile: () -> Unit,
    val openPaymentMethods: () -> Unit,
    val openAddPaymentMethod: () -> Unit,
    val openEditPaymentMethod: (String) -> Unit,
    val openFavoriteSalons: () -> Unit,
    val openNotifications: () -> Unit,
    val openChangePassword: () -> Unit,
    val openFaq: () -> Unit,
    val openAbout: () -> Unit,
    val navigateBack: () -> Unit
)

@Composable
fun ProfileNav(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val viewModel: ProfileViewModel = hiltViewModel()
    ProfileNavHost(
        navController = navController,
        viewModel = viewModel,
        onLogout = onLogout,
        modifier = modifier
    )
}

@Composable
internal fun ProfileNavHost(
    navController: NavHostController,
    viewModel: ProfileViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val actions = remember(navController, viewModel) {
        ProfileNavActions(
            openMyProfile = {
                navController.navigateSingleTopTo(ProfileDestination.MyProfile.route)
            },
            openEditProfile = {
                viewModel.resetEditForm()
                navController.navigateSingleTopTo(ProfileDestination.EditProfile.route)
            },
            openPaymentMethods = {
                navController.navigateSingleTopTo(ProfileDestination.PaymentMethods.route)
            },
            openAddPaymentMethod = {
                viewModel.prepareNewPaymentMethod()
                navController.navigateSingleTopTo(ProfileDestination.AddPaymentMethod.route)
            },
            openEditPaymentMethod = { methodId ->
                viewModel.prepareEditPaymentMethod(methodId)
                navController.navigateSingleTopTo(ProfileDestination.EditPaymentMethod.createRoute(methodId))
            },
            openFavoriteSalons = {
                viewModel.refreshFavoriteSalons()
                navController.navigateSingleTopTo(ProfileDestination.FavoriteSalons.route)
            },
            openNotifications = {
                viewModel.refreshNotifications()
                navController.navigateSingleTopTo(ProfileDestination.Notifications.route)
            },
            openChangePassword = {
                navController.navigateSingleTopTo(ProfileDestination.ChangePassword.route)
            },
            openFaq = {
                navController.navigateSingleTopTo(ProfileDestination.Faq.route)
            },
            openAbout = {
                navController.navigateSingleTopTo(ProfileDestination.About.route)
            },
            navigateBack = {
                if (!navController.popBackStack()) {
                    navController.navigateSingleTopTo(ProfileDestination.Home.route)
                }
            }
        )
    }

    NavHost(
        navController = navController,
        startDestination = ProfileDestination.Home.route,
        modifier = modifier
    ) {
        composable(ProfileDestination.Home.route) {
            val uiState by viewModel.uiState.collectAsState()
            LaunchedEffect(uiState.isLoggedOut) {
                if (uiState.isLoggedOut) {
                    onLogout()
                    viewModel.onLogoutHandled()
                    navController.popBackStack(ProfileDestination.Home.route, inclusive = false)
                }
            }
            ProfileScreen(
                uiState = uiState,
                onNavigateToMyProfile = actions.openMyProfile,
                onNavigateToPaymentMethods = actions.openPaymentMethods,
                onNavigateToFavoriteSalons = actions.openFavoriteSalons,
                onNavigateToNotifications = actions.openNotifications,
                onNavigateToChangePassword = actions.openChangePassword,
                onNavigateToFaq = actions.openFaq,
                onNavigateToAbout = actions.openAbout,
                onConfirmLogout = viewModel::logout
            )
        }
        composable(ProfileDestination.MyProfile.route) {
            val uiState by viewModel.uiState.collectAsState()
            val profile = uiState.profile
            if (profile != null) {
                MyProfileScreen(
                    profile = profile,
                    onBack = actions.navigateBack,
                    onEditProfile = actions.openEditProfile
                )
            } else {
                ProfileScreen(
                    uiState = uiState,
                    onNavigateToMyProfile = { },
                    onNavigateToPaymentMethods = { },
                    onNavigateToFavoriteSalons = { },
                    onNavigateToNotifications = { },
                    onNavigateToChangePassword = { },
                    onNavigateToFaq = { },
                    onNavigateToAbout = { },
                    onConfirmLogout = { }
                )
            }
        }
        composable(ProfileDestination.EditProfile.route) {
            val uiState by viewModel.uiState.collectAsState()
            EditProfileScreen(
                uiState = uiState,
                onBack = actions.navigateBack,
                onFieldChange = viewModel::onEditFieldChange,
                onGenderSelected = viewModel::onGenderSelected,
                onSave = viewModel::saveProfile,
                onProfileSaved = {
                    viewModel.onProfileUpdateConsumed()
                    actions.navigateBack()
                }
            )
        }
        composable(ProfileDestination.PaymentMethods.route) {
            val paymentState by viewModel.paymentMethodsState.collectAsState()
            PaymentMethodsScreen(
                state = paymentState,
                onBack = actions.navigateBack,
                onAddPaymentMethod = actions.openAddPaymentMethod,
                onEditPaymentMethod = { method -> actions.openEditPaymentMethod(method.id) },
                onDeletePaymentMethod = viewModel::requestDeletePaymentMethod,
                onDismissDelete = viewModel::dismissDeletePaymentMethod,
                onConfirmDelete = viewModel::confirmDeletePaymentMethod
            )
            LaunchedEffect(paymentState.isDeleted) {
                if (paymentState.isDeleted) {
                    viewModel.onPaymentMethodDeletedConsumed()
                }
            }
        }
        composable(ProfileDestination.AddPaymentMethod.route) {
            val paymentState by viewModel.paymentMethodsState.collectAsState()
            LaunchedEffect(Unit) {
                viewModel.prepareNewPaymentMethod()
            }
            PaymentMethodFormScreen(
                title = "Agregar tarjeta",
                actionText = "Guardar",
                state = paymentState,
                onBack = actions.navigateBack,
                onFieldChange = viewModel::onPaymentMethodFormChange,
                onSubmit = viewModel::savePaymentMethod
            )
            LaunchedEffect(paymentState.isSaved) {
                if (paymentState.isSaved) {
                    viewModel.onPaymentMethodSavedConsumed()
                    actions.navigateBack()
                }
            }
        }
        composable(
            route = ProfileDestination.EditPaymentMethod.route,
            arguments = listOf(navArgument(ProfileDestination.EditPaymentMethod.METHOD_ID) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val paymentState by viewModel.paymentMethodsState.collectAsState()
            val methodId = backStackEntry.arguments?.getString(ProfileDestination.EditPaymentMethod.METHOD_ID)
            LaunchedEffect(methodId) {
                if (methodId != null) {
                    viewModel.prepareEditPaymentMethod(methodId)
                }
            }
            PaymentMethodFormScreen(
                title = "Editar tarjeta",
                actionText = "Actualizar",
                state = paymentState,
                onBack = actions.navigateBack,
                onFieldChange = viewModel::onPaymentMethodFormChange,
                onSubmit = viewModel::savePaymentMethod
            )
            LaunchedEffect(paymentState.isSaved) {
                if (paymentState.isSaved) {
                    viewModel.onPaymentMethodSavedConsumed()
                    actions.navigateBack()
                }
            }
        }
        composable(ProfileDestination.FavoriteSalons.route) {
            val favoritesState by viewModel.favoriteSalonsState.collectAsState()
            FavoriteSalonsScreen(
                state = favoritesState,
                onBack = actions.navigateBack,
                onBookNow = { },
                onRemoveFavorite = viewModel::requestRemoveFavorite,
                onDismissRemoval = viewModel::dismissRemoveFavorite,
                onConfirmRemoval = viewModel::confirmRemoveFavorite
            )
        }
        composable(ProfileDestination.Notifications.route) {
            val notificationsState by viewModel.notificationsState.collectAsState()
            NotificationsScreen(
                state = notificationsState,
                onBack = actions.navigateBack,
                onToggleMute = viewModel::toggleNotificationMute,
                onClearAll = viewModel::clearNotifications,
                onNotificationClick = viewModel::markNotificationAsRead
            )
        }
        composable(ProfileDestination.ChangePassword.route) {
            val state by viewModel.changePasswordState.collectAsState()
            ChangePasswordScreen(
                state = state,
                onBack = actions.navigateBack,
                onFieldChange = viewModel::onChangePasswordFieldChange,
                onSubmit = viewModel::changePassword,
                onMessageConsumed = viewModel::onChangePasswordMessageConsumed
            )
        }
        composable(ProfileDestination.Faq.route) {
            FaqScreen(onBack = actions.navigateBack)
        }
        composable(ProfileDestination.About.route) {
            AboutUsScreen(onBack = actions.navigateBack)
        }
    }
}

private fun NavController.navigateSingleTopTo(route: String) {
    navigate(route) {
        launchSingleTop = true
        restoreState = true
    }
}