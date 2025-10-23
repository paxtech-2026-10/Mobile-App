package com.paxtech.mobileapp.features.clientDashboard.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.paxtech.mobileapp.shared.model.Salon

private fun mockTrendingSalons(): List<Salon> = listOf(
    Salon(101, "Glow & Go Hair", "https://images.unsplash.com/photo-1582095133179-bfd08e2fc6b3?q=80&w=1200"),
    Salon(102, "Estilo Único",   "https://images.unsplash.com/photo-1580136579312-94651dfd596d?q=80&w=1200"),
    Salon(103, "Urban Cuts",     "https://images.unsplash.com/photo-1582095133429-3e73b1a5b9b0?q=80&w=1200")
)

private fun mockRecentSalons(): List<Salon> = listOf(
    Salon(201, "Hair Loft",      "https://images.unsplash.com/photo-1582095133185-4c9c4c94a9b0?q=80&w=1200"),
    Salon(202, "Spa Serena",     "https://images.unsplash.com/photo-1580618672591-eb180b1a973f?q=80&w=1200"),
    Salon(203, "Glam & Glow",    "https://images.unsplash.com/photo-1556228578-8c89e6adf883?q=80&w=1200")
)


@Composable
fun Home(
    viewModel: HomeViewModel = hiltViewModel(),
    onSalonClick: (Int) -> Unit = {}
) {
val salons by viewModel.salons.collectAsState()

    val trending = remember { mockTrendingSalons() }
    val recents  = remember { mockRecentSalons() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        // header
        Text(
            text = "Hola, Camila",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Debug info
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = "🔍 Debug: ${salons.size} salones del API",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { viewModel.getAllSalons() },
                modifier = Modifier.height(32.dp)
            ) {
                Text("Refresh", style = MaterialTheme.typography.bodySmall)
            }
        }

        // section title
        Text(
            text = "Recomendado",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        // horizontal list with your SalonCard
        LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)) {
            items(salons) { salon: Salon ->
                SalonCard(
                    salon = salon,
                    onClick = { onSalonClick(salon.id) }
                )
            }
        }

        // --- Tendencia (mock) ---
        Text(
            text = "Tendencia",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
        LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)) {
            items(trending) { salon ->
                SalonCard(
                    salon = salon,
                    onClick = { onSalonClick(salon.id) }
                )
            }
        }

        // --- Visto recientemente (mock) ---
        Text(
            text = "Visto recientemente",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
        LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)) {
            items(recents) { salon ->
                SalonCard(
                    salon = salon,
                    onClick = { onSalonClick(salon.id) }
                )
            }
        }
    }
}



