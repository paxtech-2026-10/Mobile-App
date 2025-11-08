package com.paxtech.mobileapp.features.authentication.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paxtech.mobileapp.R
import com.paxtech.mobileapp.ui.theme.DarkPurple
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToWelcome: () -> Unit = {}
) {
    var showSplash by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        delay(2000) // Mostrar splash por 2 segundos
        showSplash = false
        onNavigateToWelcome()
    }
    
    if (showSplash) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkPurple)
                .statusBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Círculo blanco con logo de uTime
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Logo de uTime
                        Image(
                            painter = painterResource(id = R.drawable.utime_icon),
                            contentDescription = "uTime Logo",
                            modifier = Modifier.size(120.dp)
                        )
                        
                        // Texto "uTIME" en rosa
                        Text(
                            text = "uTIME",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkPurple
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}
