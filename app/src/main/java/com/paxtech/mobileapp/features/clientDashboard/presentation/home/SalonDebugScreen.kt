package com.paxtech.mobileapp.features.clientDashboard.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
//Para testear si agarra
/*
@Composable
fun SalonDebugScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val salons by viewModel.salons.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Salon API check",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(text = "Found ${salons.size} salons")

        if (salons.isNotEmpty()) {
            Text(text = "First: ${salons.first().companyName}")
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { viewModel.getAllSalons() }) {
                Text("Refresh")
            }
        }
    }
}
/*
@Preview
@Composable
fun SalonDebugScreenPreview() {
    SalonDebugScreen()
}*/

 */
