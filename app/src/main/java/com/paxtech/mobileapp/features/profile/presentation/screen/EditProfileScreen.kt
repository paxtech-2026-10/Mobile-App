package com.paxtech.mobileapp.features.profile.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paxtech.mobileapp.features.profile.presentation.model.GenderUi
import com.paxtech.mobileapp.features.profile.presentation.model.ProfileFormField
import com.paxtech.mobileapp.features.profile.presentation.model.ProfileUiState
import com.paxtech.mobileapp.features.profile.presentation.model.sample
import com.paxtech.mobileapp.ui.theme.BackgroundWhite
import com.paxtech.mobileapp.ui.theme.PrimaryPurple
import com.paxtech.mobileapp.ui.theme.TextPrimary
import com.paxtech.mobileapp.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    uiState: ProfileUiState,
    onBack: () -> Unit,
    onFieldChange: (ProfileFormField, String) -> Unit,
    onGenderSelected: (GenderUi) -> Unit,
    onSave: () -> Unit,
    onProfileSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(uiState.isProfileUpdated) {
        if (uiState.isProfileUpdated) {
            onProfileSaved()
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = BackgroundWhite,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Editar perfil",
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BackgroundWhite,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = TextPrimary
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (uiState.isSaving) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = PrimaryPurple
                )
            }

            val form = uiState.editForm

            OutlinedTextField(
                value = form.fullName,
                onValueChange = { onFieldChange(ProfileFormField.FullName, it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Nombre completo", color = TextSecondary) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Words
                )
            )

            OutlinedTextField(
                value = form.email,
                onValueChange = { onFieldChange(ProfileFormField.Email, it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Correo electrónico", color = TextSecondary) },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email
                )
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = form.countryCode,
                    onValueChange = {
                        onFieldChange(
                            ProfileFormField.CountryCode,
                            it.filterCountryCode()
                        )
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text(text = "Código", color = TextSecondary) },
                    singleLine = true
                )
                OutlinedTextField(
                    value = form.phoneNumber,
                    onValueChange = {
                        onFieldChange(
                            ProfileFormField.PhoneNumber,
                            it.filterPhoneNumber()
                        )
                    },
                    modifier = Modifier.weight(2f),
                    label = { Text(text = "Teléfono", color = TextSecondary) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Phone
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Selecciona tu género",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GenderUi.values().forEach { gender ->
                        val selected = form.gender == gender
                        FilterChip(
                            selected = selected,
                            onClick = { onGenderSelected(gender) },
                            label = {
                                Text(
                                    text = gender.label,
                                    color = if (selected) BackgroundWhite else TextSecondary
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryPurple,
                                containerColor = BackgroundWhite
                            )
                        )
                    }
                }
            }

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                enabled = form.isValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryPurple,
                    contentColor = BackgroundWhite
                )
            ) {
                Text(text = "Actualizar")
            }
        }
    }
}

private fun String.filterCountryCode(): String =
    this.filter { it == '+' || it.isDigit() }.take(4)

private fun String.filterPhoneNumber(): String =
    this.filter { it.isDigit() }.take(15)

@Preview(showBackground = true)
@Composable
private fun EditProfileScreenPreview() {
    MaterialTheme {
        Surface(color = BackgroundWhite) {
            EditProfileScreen(
                uiState = ProfileUiState.sample(),
                onBack = {},
                onFieldChange = { _, _ -> },
                onGenderSelected = {},
                onSave = {},
                onProfileSaved = {}
            )
        }
    }
}
