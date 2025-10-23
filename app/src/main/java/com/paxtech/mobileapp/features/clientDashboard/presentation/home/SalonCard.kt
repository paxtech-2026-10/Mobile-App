package com.paxtech.mobileapp.features.clientDashboard.presentation.home

import android.R.attr.label
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.paxtech.mobileapp.shared.model.Salon

@Composable
fun SalonCard(
    salon: Salon,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(220.dp),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(10.dp)) {
            AsyncImage(model = salon.coverImageUrl,
                contentDescription = salon.companyName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop

            )

            Spacer(Modifier.height(8.dp))

            Text(text = salon.companyName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE1BEE7), // Morado suave
                        contentColor = Color(0xFF4A148C) // Morado oscuro para el texto
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Book Now")
                }
            }

        }
    }
}

fun MockSalon() = Salon(
    id =1,
    companyName = "Tijeras",
    coverImageUrl = "https://images.unsplash.com/photo-1556228578-8c89e6adf883?q=80&w=1200"
)
@Preview(showBackground = true)
@Composable
fun SalonCardPreview(){
    SalonCard(salon = MockSalon(), onClick = {})
}