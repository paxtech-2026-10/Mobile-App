package com.paxtech.mobileapp.features.clientDashboard.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.paxtech.mobileapp.shared.model.Salon

// ------------------------ MOCKS ORIGINALES ------------------------
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

// ------------------------ PROVEEDOR DE MOCKS (usado por otras pantallas) ------------------------
object HomeMockProvider {
    // Puedes añadir aquí si también quieres incluir los que vienen del repo.
    val all: List<Salon> = (mockTrendingSalons() + mockRecentSalons())
    fun getById(id: Int): Salon? = all.firstOrNull { it.id == id }
}

// ------------------------ UI ------------------------
@Composable
fun Home(
    viewModel: HomeViewModel = hiltViewModel(),
    onSalonClick: (Int) -> Unit = {}
) {
    // Si quieres seguir mostrando lo del repo en "Recomendado":
    val salons by viewModel.salons.collectAsState()

    val trending = remember { mockTrendingSalons() }
    val recents  = remember { mockRecentSalons() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Hola, Camila",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Text(
            text = "Recomendado",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)) {
            items(salons) { salon: Salon ->
                SalonCard(
                    salon = salon,
                    onClick = { onSalonClick(salon.id) }
                )
            }
        }

        Text(
            text = "Tendencia",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
        LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)) {
            items(trending) { salon ->
                SalonCard(salon = salon, onClick = { onSalonClick(salon.id) })
            }
        }

        Text(
            text = "Visto recientemente",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
        LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)) {
            items(recents) { salon ->
                SalonCard(salon = salon, onClick = { onSalonClick(salon.id) })
            }
        }
    }
}
