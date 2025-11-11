package com.paxtech.mobileapp.features.services.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.paxtech.mobileapp.features.services.domain.Service
import com.paxtech.mobileapp.shared.model.ServiceResult

@Preview(showBackground = true)
@Composable
fun SearchServiceView(
    viewModel: SearchServiceViewModel = hiltViewModel()
) {

    val categories = listOf<String>(
        "Maquillaje", "Manicure", "Barberia", "Cuidado Facial", "Masajes",
        "Coloracion & Mechas", "Corte de Cabello", "Peinados", "Alisados",
        "Hidratacion & Mascarillas", "Permanentes", "Pestanas & Cejas")

    val query = viewModel.query.collectAsState()
    val services = viewModel.services.collectAsState()

    val servicios = listOf<ServiceResult>(
        ServiceResult( 1, "nat", 1000, 500, 1 ),
        ServiceResult( 1, "nat", 1000, 500, 2 ),
        ServiceResult( 1, "nat", 1000, 500, 3 )
    )

    val isSearchBarActive = remember {
        mutableStateOf<Boolean>(false)
    }

    Column(
        modifier = Modifier
        .fillMaxSize().padding(top = 16.dp)
    ){
        if (isSearchBarActive.value) {
            IconButton(onClick = {
                isSearchBarActive.value = false
            }) {
                Icon(Icons.Default.ArrowBackIosNew, contentDescription = null)
            }
        }
        OutlinedTextField(
            value = query.value,
            onValueChange = {
                viewModel.onChangeQuery(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            trailingIcon = {
                IconButton(onClick = {
                    isSearchBarActive.value = true
                    viewModel.searchService()
                }) {
                    Icon(Icons.Default.Search, contentDescription = null)
                }
            }
        )
        Column(
            modifier = Modifier.padding(top = 32.dp)
        ) {
            if (!isSearchBarActive.value) {
                Box(modifier = Modifier
                    .padding(bottom = 12.dp)
                    .padding(horizontal = 8.dp)
                ) {
                    Text(text = "Categorias",
                        style = MaterialTheme.typography.headlineLarge)
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(categories) { category ->
                        CategoryCard(
                            title = category,
                            onClick = {
                                // Establecer el query con el nombre de la categoría
                                viewModel.onChangeQuery(category)
                                // Realizar la búsqueda automática
                                viewModel.searchService()
                                // Activar la vista de resultados
                                isSearchBarActive.value = true
                            }
                        )
                    }
                }
            }
            if (isSearchBarActive.value) {
                Box(modifier = Modifier
                    .padding(bottom = 12.dp)
                    .padding(horizontal = 8.dp)
                ) {
                    Text(text = "Resultados",
                        style = MaterialTheme.typography.headlineLarge)
                }
                LazyColumn(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(services.value) { service ->
                        ServiceCard(service)
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    title: String,
    onClick: () -> Unit = {}
) {
    Card (
        modifier = Modifier
            .border(width = 2.dp, color = MaterialTheme.colorScheme.onPrimaryFixedVariant, shape = CardDefaults.elevatedShape)
            .padding(4.dp)
            .height(70.dp)
            .clickable(onClick = onClick)
    ){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,

        ){
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.onPrimaryFixedVariant,
                    fontFamily = FontFamily.Cursive
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ServiceCard(service: Service) {
    Card (
        modifier = Modifier
            .border(width = 2.dp, color = MaterialTheme.colorScheme.onPrimaryFixedVariant, shape = CardDefaults.elevatedShape)
            .fillMaxWidth()
            .height(180.dp)
    ){
        Box(
            modifier = Modifier.padding(horizontal = 16.dp).padding(vertical = 4.dp)
        ){
            Text(
                text = service.name,
                style = MaterialTheme.typography.headlineMedium
            )
        }
        Box(
            modifier = Modifier.padding(horizontal = 16.dp)
        ){
            Text(
                text = "Aprox duration: " + service.duration + " minutes",
                style = MaterialTheme.typography.titleLarge
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.padding(vertical = 8.dp)
            ){
                Text(
                    text = "Salon: " + service.providerId,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Box(
                modifier = Modifier.padding(vertical = 8.dp)
            ){
                Text(
                    text = "Price S/: " + service.price,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth().height(70.dp).padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            ElevatedButton(
                onClick = {}
            ) {
                Text("Reservar ahora")
            }
        }
    }
}
