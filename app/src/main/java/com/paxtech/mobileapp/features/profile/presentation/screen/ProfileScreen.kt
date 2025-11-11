package com.paxtech.mobileapp.features.profile.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paxtech.mobileapp.features.profile.presentation.components.ProfileAvatar
import com.paxtech.mobileapp.features.profile.presentation.model.ProfileUi
import com.paxtech.mobileapp.features.profile.presentation.model.ProfileUiState
import com.paxtech.mobileapp.features.profile.presentation.model.sample
import com.paxtech.mobileapp.ui.theme.BackgroundGray
import com.paxtech.mobileapp.ui.theme.BackgroundWhite
import com.paxtech.mobileapp.ui.theme.DividerGray
import com.paxtech.mobileapp.ui.theme.LightPurple
import com.paxtech.mobileapp.ui.theme.PrimaryPurple
import com.paxtech.mobileapp.ui.theme.TextPrimary
import com.paxtech.mobileapp.ui.theme.TextSecondary

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onNavigateToMyProfile: () -> Unit,
    onNavigateToPaymentMethods: () -> Unit,
    onNavigateToFavoriteSalons: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToFaq: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onConfirmLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Surface(color = BackgroundWhite) {
        when {
            uiState.isLoading && uiState.profile == null -> {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.profile != null -> {
                ProfileContent(
                    profile = uiState.profile,
                    onNavigateToMyProfile = onNavigateToMyProfile,
                    onNavigateToPaymentMethods = onNavigateToPaymentMethods,
                    onNavigateToFavoriteSalons = onNavigateToFavoriteSalons,
                    onNavigateToNotifications = onNavigateToNotifications,
                    onNavigateToChangePassword = onNavigateToChangePassword,
                    onNavigateToFaq = onNavigateToFaq,
                    onNavigateToAbout = onNavigateToAbout,
                    onLogoutClick = { showLogoutDialog = true },
                    modifier = modifier
                )
            }

            else -> {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.errorMessage ?: "No fue posible cargar la información",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onConfirmLogout()
                    }
                ) {
                    Text(text = "Sí, cerrar sesión", color = PrimaryPurple)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(text = "Cancelar", color = TextSecondary)
                }
            },
            title = {
                Text(
                    text = "Cerrar sesión",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = "¿Deseas cerrar sesión en tu cuenta?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        )
    }
}

@Composable
private fun ProfileContent(
    profile: ProfileUi,
    onNavigateToMyProfile: () -> Unit,
    onNavigateToPaymentMethods: () -> Unit,
    onNavigateToFavoriteSalons: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToFaq: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 24.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ProfileHeader(
            profile = profile,
            onNavigateToMyProfile = onNavigateToMyProfile
        )

        ProfileMenu(
            onNavigateToMyProfile = onNavigateToMyProfile,
            onNavigateToPaymentMethods = onNavigateToPaymentMethods,
            onNavigateToFavoriteSalons = onNavigateToFavoriteSalons,
            onNavigateToNotifications = onNavigateToNotifications,
            onNavigateToChangePassword = onNavigateToChangePassword,
            onNavigateToFaq = onNavigateToFaq,
            onNavigateToAbout = onNavigateToAbout,
            onLogoutClick = onLogoutClick
        )
    }
}

@Composable
private fun ProfileHeader(
    profile: ProfileUi,
    onNavigateToMyProfile: () -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            PrimaryPurple,
                            LightPurple
                        )
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileAvatar(
                        avatarUrl = profile.avatarUrl,
                        containerColor = BackgroundWhite.copy(alpha = 0.15f),
                        contentColor = BackgroundWhite
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = profile.fullName,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = BackgroundWhite
                        )
                        Text(
                            text = profile.email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = BackgroundWhite.copy(alpha = 0.8f)
                        )
                    }
                }

                TextButton(onClick = onNavigateToMyProfile) {
                    Text(text = "Ver mi perfil", color = BackgroundWhite)
                }
            }
        }
    }
}

@Composable
private fun ProfileMenu(
    onNavigateToMyProfile: () -> Unit,
    onNavigateToPaymentMethods: () -> Unit,
    onNavigateToFavoriteSalons: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToFaq: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    val menuItems = listOf(
        ProfileMenuItem(
            icon = Icons.Filled.Person,
            title = "Mi perfil",
            onClick = onNavigateToMyProfile
        ),
        ProfileMenuItem(
            icon = Icons.Filled.CreditCard,
            title = "Métodos de pago",
            onClick = onNavigateToPaymentMethods
        ),
        ProfileMenuItem(
            icon = Icons.Filled.Favorite,
            title = "Salones favoritos",
            onClick = onNavigateToFavoriteSalons
        ),
        ProfileMenuItem(
            icon = Icons.Filled.Notifications,
            title = "Notificaciones",
            onClick = onNavigateToNotifications
        ),
        ProfileMenuItem(
            icon = Icons.Filled.Lock,
            title = "Cambiar contraseña",
            onClick = onNavigateToChangePassword
        ),
        ProfileMenuItem(
            icon = Icons.Filled.HelpOutline,
            title = "Preguntas frecuentes",
            onClick = onNavigateToFaq
        ),
        ProfileMenuItem(
            icon = Icons.Filled.Info,
            title = "Sobre nosotros",
            onClick = onNavigateToAbout
        ),
        ProfileMenuItem(
            icon = Icons.Filled.Logout,
            title = "Cerrar sesión",
            onClick = onLogoutClick,
            tint = MaterialTheme.colorScheme.error,
            textColor = MaterialTheme.colorScheme.error,
            showArrow = false
        )
    )

    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundGray)
    ) {
        Column {
            menuItems.forEachIndexed { index, item ->
                ProfileMenuRow(
                    item = item,
                    showDivider = index != menuItems.lastIndex
                )
            }
        }
    }
}

private data class ProfileMenuItem(
    val icon: ImageVector,
    val title: String,
    val onClick: (() -> Unit)? = null,
    val tint: Color? = null,
    val textColor: Color? = null,
    val showArrow: Boolean = true
)

@Composable
private fun ProfileMenuRow(
    item: ProfileMenuItem,
    showDivider: Boolean,
) {
    val iconTint = item.tint ?: PrimaryPurple
    val labelColor = item.textColor ?: TextPrimary

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = item.onClick != null) {
                    item.onClick?.invoke()
                }
                .padding(horizontal = 24.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = iconTint
                )
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = labelColor
                )
            }
            if (item.showArrow) {
                Icon(
                    imageVector = Icons.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = TextSecondary
                )
            }
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 24.dp),
                color = DividerGray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    MaterialTheme {
        Surface {
            ProfileScreen(
                uiState = ProfileUiState.sample(),
                onNavigateToMyProfile = {},
                onNavigateToPaymentMethods = {},
                onNavigateToFavoriteSalons = {},
                onNavigateToNotifications = {},
                onNavigateToChangePassword = {},
                onNavigateToFaq = {},
                onNavigateToAbout = {},
                onConfirmLogout = {}
            )
        }
    }
}

